package com.extole.common.rest.client;

import static java.util.stream.Collectors.joining;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.client.exception.translation.NoopRestExceptionTranslationStrategy;
import com.extole.common.rest.client.exception.translation.RestExceptionTranslationStrategy;
import com.extole.common.rest.time.TimeZoneParam;

// copy of https://github.com/jersey/jersey/pull/235/files with additional support for @AccessTokenParam
public final class WebResourceFactory implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebResourceFactory.class);

    private static final String[] EMPTY_STRING_ARRAY = {};

    private final WebTarget target;
    private final MultivaluedMap<String, Object> headers;
    private final List<Cookie> cookies;
    private final Multimap<String, String> queryParameters;
    private final Form form;
    private final RestExceptionTranslationStrategy restExceptionTranslationStrategy;
    private final ObjectMapper objectMapper;

    private static final List<Class<?>> PARAM_ANNOTATION_CLASSES = Arrays.asList(PathParam.class, QueryParam.class,
        HeaderParam.class, CookieParam.class, MatrixParam.class, FormParam.class, FormDataParam.class, BeanParam.class,
        AccessTokenParam.class, UserAccessTokenParam.class, TimeZoneParam.class);

    private WebResourceFactory(
        WebTarget target,
        MultivaluedMap<String, Object> headers,
        List<Cookie> cookies,
        Multimap<String, String> queryParameters,
        Form form,
        RestExceptionTranslationStrategy restExceptionTranslationStrategy,
        ObjectMapper objectMapper) {
        this.target = target;
        this.headers = new ImmutableMultivaluedMap<>(headers);
        this.cookies = ImmutableList.copyOf(cookies);
        this.queryParameters = ImmutableListMultimap.copyOf(queryParameters);
        this.form = form;
        this.restExceptionTranslationStrategy = restExceptionTranslationStrategy;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (args == null && method.getName().equals("toString")) {
            return toString();
        }

        if (args != null && args.length == 1 && method.getName().equals("equals")) {
            return equals(args[0]);
        }

        if (args == null && method.getName().equals("hashCode")) {
            return hashCode();
        }

        Class<?> mask = proxy.getClass().getInterfaces()[0];

        String arguments = args == null ? StringUtils.EMPTY
            : Arrays.stream(args)
                .map(String::valueOf)
                .collect(joining(","));

        LOG.info("Calling {}.{}({})", mask.getSimpleName(), method.getName(), arguments);

        // get the interface describing the resource
        final Class<?> proxyIfc = proxy.getClass().getInterfaces()[0];

        // response type
        final Class<?> responseType = method.getReturnType();

        // determine method name
        String httpMethod = getHttpMethodName(method);
        if (httpMethod == null) {
            for (final Annotation ann : method.getAnnotations()) {
                httpMethod = getHttpMethodName(ann.annotationType());
                if (httpMethod != null) {
                    break;
                }
            }
        }

        // create a new UriBuilder appending the @Path attached to the method
        WebTarget newTarget = addPathFromAnnotation(method, target);

        if (httpMethod == null) {
            if (newTarget == target) {
                // no path annotation on the method -> fail
                throw new UnsupportedOperationException("Not a resource method.");
            } else if (!responseType.isInterface()) {
                // the method is a subresource locator, but returns class,
                // not interface - can't help here
                throw new UnsupportedOperationException("Return type not an interface");
            }
        }

        MultivaluedMap<String, Object> localHeaders = new MultivaluedHashMap<>(headers);
        List<Cookie> localCookies = new ArrayList<>(cookies);
        Form localForm = new Form(new MultivaluedHashMap<>(form.asMap()));

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        // process method params (build maps of (Path|Form|Cookie|Matrix|Header..)Params
        // and extract entity type
        // final MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>(this.headers);
        // final LinkedList<Cookie> cookies = new LinkedList<>(this.cookies);
        // final Form form = new Form();
        // form.asMap().putAll(this.form.asMap());
        final Annotation[][] paramAnns = method.getParameterAnnotations();
        Object entity = null;
        Type entityType = null;

        for (int i = 0; i < paramAnns.length; i++) {
            final Map<Class, Annotation> anns = getAnnotationsMap(paramAnns[i]);
            Object value = args[i];
            if (!hasAnyParamAnnotation(anns)) {
                entityType = method.getGenericParameterTypes()[i];
                entity = value;
            } else {
                newTarget = setupParameter(method.getParameterTypes()[i], anns, localHeaders, localCookies, localForm,
                    formDataMultiPart, newTarget, value);
            }
        }

        if (httpMethod == null) {
            // the method is a subresource locator
            return WebResourceFactory.newResource(responseType, newTarget, true, localHeaders, localCookies,
                queryParameters, localForm, restExceptionTranslationStrategy, objectMapper);
        }

        // accepted media types
        Produces produces = method.getAnnotation(Produces.class);
        if (produces == null) {
            produces = proxyIfc.getAnnotation(Produces.class);
        }
        final String[] accepts = (produces == null) ? EMPTY_STRING_ARRAY : produces.value();

        // determine content type
        String contentType = null;
        if (entity != null) {
            final List<Object> contentTypeEntries = localHeaders.get(HttpHeaders.CONTENT_TYPE);
            if ((contentTypeEntries != null) && (!contentTypeEntries.isEmpty())) {
                contentType = contentTypeEntries.get(0).toString();
            } else {
                Consumes consumes = method.getAnnotation(Consumes.class);
                if (consumes == null) {
                    consumes = proxyIfc.getAnnotation(Consumes.class);
                }
                if (consumes != null && consumes.value().length > 0) {
                    contentType = consumes.value()[0];
                }
            }
        }

        for (String parameterName : queryParameters.keySet()) {
            Object[] parameterValues = queryParameters.get(parameterName).toArray();
            newTarget = newTarget.queryParam(parameterName, parameterValues);
        }
        Invocation.Builder builder = newTarget.request()
            .headers(localHeaders) // this resets all headers so do this first
            .accept(accepts); // if @Produces is defined, propagate values into Accept header; empty array is NO-OP

        for (final Cookie c : localCookies) {
            builder = builder.cookie(c);
        }

        final Object result;

        if (entity == null && !localForm.asMap().isEmpty()) {
            entity = localForm;
            contentType = MediaType.APPLICATION_FORM_URLENCODED;
        } else {
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
            if (!localForm.asMap().isEmpty()) {
                if (entity instanceof Form) {
                    ((Form) entity).asMap().putAll(localForm.asMap());
                } else {
                    // TODO should at least log some warning here
                }
            }
        }

        final GenericType<?> responseGenericType = getGenericTypeOfMethodReturnType(method);
        try (formDataMultiPart) {
            try {
                if (entity != null) {
                    Object requestEntity = getRequestEntity(entity, entityType, contentType);
                    result = builder.method(httpMethod, Entity.entity(requestEntity, contentType), responseGenericType);
                } else if (!formDataMultiPart.getFields().isEmpty()) {
                    Entity<FormDataMultiPart> multiPartEntity =
                        Entity.entity(formDataMultiPart, MediaType.MULTIPART_FORM_DATA_TYPE);
                    result = builder.method(httpMethod, multiPartEntity, responseGenericType);
                } else {
                    result = builder.method(httpMethod, responseGenericType);
                }

                return remapResultToResponseIfNeeded(result)
                    .map(Object.class::cast)
                    .orElse(result);
            } catch (ClientErrorException e) {
                Class<? extends Exception>[] exceptionTypes = (Class<? extends Exception>[]) method.getExceptionTypes();
                throw restExceptionTranslationStrategy.translateException(Arrays.asList(exceptionTypes), e);
            }
        }
    }

    private Optional<Response> remapResultToResponseIfNeeded(Object result) {
        if (result instanceof Response) {
            Response response = (Response) result;
            ManagedJaxrsResponse managedResponse = ManagedJaxrsResponse
                .builder(response)
                .withEntityReader(byte[].class, Function.identity())
                .withEntityReader(String.class, String::new)
                .withEntityReader(InputStream.class, ByteArrayInputStream::new)
                .build();
            response.close();

            if (managedResponse.getStatusInfo().getFamily() == Family.CLIENT_ERROR) {
                throw new ClientErrorException(managedResponse);
            }
            return Optional.of(managedResponse);
        }
        return Optional.empty();
    }

    private Object getRequestEntity(Object entity, Type entityType, String contentType)
        throws JsonProcessingException, UnsupportedEncodingException {
        boolean isRepeatableReadEntity = checkRepeatableReadEntity(entity);
        if (isRepeatableReadEntity) {
            return getRepeatableReadEntity(entity, contentType);
        }
        return getNonRepeatableReadEntity(entity, entityType);
    }

    private boolean checkRepeatableReadEntity(Object entity) {
        if (hasInputStreamField(entity.getClass())) {
            return false;
        }
        return ClassUtils.getAllSuperclasses(entity.getClass()).stream()
            .noneMatch(clazz -> hasInputStreamField(clazz));
    }

    private boolean hasInputStreamField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive()
                || field.getType().isArray()) {
                continue;
            }
            if (InputStream.class.isAssignableFrom(field.getType())) {
                return true;
            } else {
                if (hasInputStreamField(field.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private HttpEntity getRepeatableReadEntity(Object entity, String contentType)
        throws UnsupportedEncodingException, JsonProcessingException {
        if (!(entity instanceof Form)) {
            return new ByteArrayEntity(objectMapper.writeValueAsBytes(entity), ContentType.parse(contentType));
        }
        Set<Map.Entry<String, List<String>>> entrySet = ((Form) entity).asMap().entrySet();
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            for (String value : entry.getValue()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }
        return new UrlEncodedFormEntity(nameValuePairs);
    }

    private Object getNonRepeatableReadEntity(Object entity, Type entityType) {
        if (entityType instanceof ParameterizedType) {
            return new GenericEntity<>(entity, entityType);
        }
        return entity;
    }

    private boolean hasAnyParamAnnotation(final Map<Class, Annotation> anns) {
        for (final Class paramAnnotationClass : PARAM_ANNOTATION_CLASSES) {
            if (anns.containsKey(paramAnnotationClass)) {
                return true;
            }
        }
        return false;
    }

    private Object[] convert(final Collection value) {
        return value.toArray();
    }

    private static WebTarget addPathFromAnnotation(final AnnotatedElement ae, WebTarget target) {
        final Path p = ae.getAnnotation(Path.class);
        if (p != null) {
            target = target.path(p.value());
        }
        return target;
    }

    private GenericType getGenericTypeOfMethodReturnType(final Method method) {
        Type genericReturnType = method.getGenericReturnType();

        if (genericReturnType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) genericReturnType;
            if (typeVariable.getBounds().length > 0) {
                return new GenericType<>(typeVariable.getBounds()[0]);
            } else {
                return new GenericType<>(Object.class);
            }
        }

        return new GenericType<>(genericReturnType);
    }

    @Override
    public String toString() {
        return target.toString();
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return target.equals(obj);
    }

    private static String getHttpMethodName(final AnnotatedElement ae) {
        final HttpMethod a = ae.getAnnotation(HttpMethod.class);
        return a == null ? null : a.value();
    }

    private WebTarget setupParameter(final Class<?> paramType,
        final Map<Class, Annotation> anns,
        final MultivaluedMap<String, Object> headers,
        final List<Cookie> cookies,
        final Form form,
        final FormDataMultiPart formDataMultiPart,
        WebTarget newTarget,
        Object value)
        throws IllegalAccessException, InvocationTargetException, IntrospectionException, IOException {

        if (value == null && anns.containsKey(DefaultValue.class)) {
            value = ((DefaultValue) anns.get(DefaultValue.class)).value();
        }

        if (Optional.class.isInstance(value)) {
            value = ((Optional) value).orElse(null);
        }

        if (value != null) {
            if (anns.containsKey(PathParam.class)) {
                newTarget = newTarget.resolveTemplate(((PathParam) anns.get(PathParam.class)).value(), value);
            } else if (anns.containsKey(AccessTokenParam.class) || anns.containsKey(UserAccessTokenParam.class)) {
                newTarget = newTarget.queryParam("access_token", value);
            } else if (anns.containsKey(TimeZoneParam.class)) {
                newTarget = newTarget.queryParam("time_zone", value);
            } else if (anns.containsKey(QueryParam.class)) {
                QueryParam queryParam = (QueryParam) anns.get(QueryParam.class);
                if (value instanceof Collection) {
                    newTarget = newTarget.queryParam(queryParam.value(), convert((Collection) value));
                } else {
                    newTarget = newTarget.queryParam(queryParam.value(), value);
                }
            } else if (anns.containsKey(HeaderParam.class)) {
                HeaderParam headerParam = (HeaderParam) anns.get(HeaderParam.class);
                if (value instanceof Collection) {
                    headers.addAll(headerParam.value(), convert((Collection) value));
                } else {
                    headers.addAll(headerParam.value(), value);
                }

            } else if (anns.containsKey(CookieParam.class)) {
                final String name = ((CookieParam) anns.get(CookieParam.class)).value();
                Cookie c;
                if (value instanceof Collection) {
                    for (final Object v : ((Collection) value)) {
                        if (!(v instanceof Cookie)) {
                            c = new Cookie(name, v.toString());
                        } else {
                            c = (Cookie) v;
                            if (!name.equals(((Cookie) v).getName())) {
                                // is this the right thing to do? or should I fail? or ignore the difference?
                                c = new Cookie(name, c.getValue(), c.getPath(), c.getDomain(), c.getVersion());
                            }
                        }
                        cookies.add(c);
                    }
                } else {
                    if (!(value instanceof Cookie)) {
                        cookies.add(new Cookie(name, value.toString()));
                    } else {
                        c = (Cookie) value;
                        if (!name.equals(((Cookie) value).getName())) {
                            // is this the right thing to do? or should I fail? or ignore the difference?
                            cookies.add(new Cookie(name, c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
                        }
                    }
                }
            } else if (anns.containsKey(MatrixParam.class)) {
                MatrixParam matrixParam = (MatrixParam) anns.get(MatrixParam.class);
                if (value instanceof Collection) {
                    newTarget = newTarget.matrixParam(matrixParam.value(), convert((Collection) value));
                } else {
                    newTarget = newTarget.matrixParam(matrixParam.value(), value);
                }
            } else if (anns.containsKey(FormParam.class)) {
                FormParam formParam = (FormParam) anns.get(FormParam.class);
                if (value instanceof Collection) {
                    for (final Object v : ((Collection) value)) {
                        form.param(formParam.value(), v.toString());
                    }
                } else {
                    form.param(formParam.value(), value.toString());
                }
            } else if (anns.containsKey(BeanParam.class)) {
                newTarget = extractParamsFromBeanParamClass(paramType, headers, cookies, form, formDataMultiPart,
                    newTarget, value);
            } else if (anns.containsKey(FormDataParam.class)) {
                FormDataParam formDataParam = (FormDataParam) anns.get(FormDataParam.class);
                if (value instanceof InputStream) {
                    formDataMultiPart.field(formDataParam.value(), value, MediaType.APPLICATION_OCTET_STREAM_TYPE);
                } else if (value instanceof FormDataContentDisposition) {
                    FormDataBodyPart field = formDataMultiPart.getField(formDataParam.value());
                    if (field != null) {
                        field.setFormDataContentDisposition((FormDataContentDisposition) value);
                    } else {
                        formDataMultiPart.field(formDataParam.value(), null)
                            .setContentDisposition((FormDataContentDisposition) value);
                    }
                } else {
                    formDataMultiPart.field(formDataParam.value(), value, MediaType.APPLICATION_JSON_TYPE);
                }

            }
        }
        return newTarget;
    }

    private WebTarget extractParamsFromBeanParamClass(final Class<?> beanParamType,
        final MultivaluedMap<String, Object> headers,
        final List<Cookie> cookies,
        final Form form,
        final FormDataMultiPart formDataMultiPart,
        WebTarget newTarget,
        final Object bean)
        throws IllegalAccessException, InvocationTargetException, IntrospectionException, IOException {
        Method[] methods = AccessController.doPrivileged(ReflectionHelper.getMethodsPA(beanParamType));
        for (Method method : methods) {
            final Map<Class, Annotation> anns =
                getAnnotationsMap(method.getAnnotations());

            if (hasAnyParamAnnotation(anns)) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                newTarget =
                    setupParameter(method.getReturnType(), anns, headers, cookies, form, formDataMultiPart, newTarget,
                        method.invoke(bean));
            }
        }

        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(beanParamType,
            Introspector.USE_ALL_BEANINFO).getPropertyDescriptors();
        for (PropertyDescriptor propertyDesc : propertyDescriptors) {
            Method beanSetterMethod = propertyDesc.getWriteMethod();
            if (beanSetterMethod != null) {
                final Map<Class, Annotation> anns =
                    getAnnotationsMap(beanSetterMethod.getAnnotations());

                if (hasAnyParamAnnotation(anns)) {
                    Method beanGetterMethod = propertyDesc.getReadMethod();
                    if (!beanGetterMethod.isAccessible()) {
                        beanGetterMethod.setAccessible(true);
                    }
                    newTarget = setupParameter(beanGetterMethod.getReturnType(), anns, headers,
                        cookies, form, formDataMultiPart, newTarget, beanGetterMethod.invoke(bean));
                }
            }
        }

        return newTarget;
    }

    private Map<Class, Annotation> getAnnotationsMap(Annotation[] annotations) {
        final Map<Class, Annotation> anns = new HashMap<>();
        for (final Annotation ann : annotations) {
            anns.put(ann.annotationType(), ann);
        }
        return anns;
    }

    /**
     * Creates a new client-side representation of a resource described by
     * the interface passed in the first argument.
     *
     * @param <C> Type of the resource to be created.
     * @param resourceInterface Interface describing the resource to be created.
     * @param target WebTarget pointing to the resource or the parent of the resource.
     * @param ignoreResourcePath If set to true, ignores path annotation on the resource interface (this is used when
     *            creating sub-resources)
     * @param headers Header params collected from parent resources (used when creating a sub-resource)
     * @param cookies Cookie params collected from parent resources (used when creating a sub-resource)
     * @param queryParameters Query parameters to be added to target (used when creating a sub-resource)
     * @param form Form params collected from parent resources (used when creating a sub-resource)
     * @param objectMapper Object mapper used to transform entity to byte array
     * @return Instance of a class implementing the resource interface that can
     *         be used for making requests to the server.
     */
    @SuppressWarnings("unchecked")
    private static <C> C newResource(final Class<C> resourceInterface,
        final WebTarget target,
        final boolean ignoreResourcePath,
        final MultivaluedMap<String, Object> headers,
        final List<Cookie> cookies,
        final Multimap<String, String> queryParameters,
        final Form form,
        final RestExceptionTranslationStrategy restExceptionTranslationStrategy,
        final ObjectMapper objectMapper) {

        return (C) Proxy.newProxyInstance(
            AccessController.doPrivileged(ReflectionHelper.getClassLoaderPA(resourceInterface)),
            new Class[] {resourceInterface},
            new WebResourceFactory(ignoreResourcePath ? target : addPathFromAnnotation(resourceInterface, target),
                headers, cookies, queryParameters, form, restExceptionTranslationStrategy, objectMapper));
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<C> {

        private Class<C> resourceInterface;
        private WebTarget target;
        private MultivaluedMap<String, Object> headers = ImmutableMultivaluedMap.empty();
        private List<Cookie> cookies = Collections.<Cookie>emptyList();
        private Multimap<String, String> queryParameters = ImmutableMultimap.of();
        private Form form = new Form();
        private RestExceptionTranslationStrategy restExceptionTranslationStrategy =
            NoopRestExceptionTranslationStrategy.getSingleton();
        private ObjectMapper objectMapper = JerseyClientConfigBuilder.DEFAULT_OBJECT_MAPPER;

        private Builder() {
        }

        public Builder<C> withResourceInterface(Class<C> resourceInterface) {
            this.resourceInterface = resourceInterface;
            return this;
        }

        public Builder<C> withTarget(WebTarget target) {
            this.target = target;
            return this;
        }

        public Builder<C> withHeaders(MultivaluedMap<String, Object> headers) {
            this.headers = new ImmutableMultivaluedMap<>(headers);
            return this;
        }

        public Builder<C> withCookies(List<Cookie> cookies) {
            this.cookies = ImmutableList.copyOf(cookies);
            return this;
        }

        public Builder<C> withQueryParameters(Multimap<String, String> queryParameters) {
            this.queryParameters = ImmutableMultimap.copyOf(queryParameters);
            return this;
        }

        public Builder<C> withForm(Form form) {
            this.form = form;
            return this;
        }

        public Builder<C>
            withRestExceptionTranslationStrategy(RestExceptionTranslationStrategy restExceptionTranslationStrategy) {
            this.restExceptionTranslationStrategy = restExceptionTranslationStrategy;
            return this;
        }

        public Builder<C> withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public C build() {
            Preconditions.checkNotNull(resourceInterface);
            Preconditions.checkNotNull(target);

            return newResource(resourceInterface, target, false, headers, cookies, queryParameters, form,
                restExceptionTranslationStrategy, objectMapper);
        }

    }

}
