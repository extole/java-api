/**
 * Classes that are available to Javascript prehandlers participating in the processing of the
 * {@link com.extole.api.event.ProcessedRawEvent}
 *
 * <h2>Prehandler Conditions</h2>
 * <p>
 * The prehandler condition should be presented by a function which returns a <code>boolean</code>.
 * The Javascript has the access to the <code>context</code> variable which
 * represents the {@link com.extole.api.prehandler.PrehandlerConditionContext}.
 * <p>
 * This is a prehandler condition example that checks the event name and the geoIp information:
 *
 * <pre>
 * context.getProcessedRawEvent().getEventName() === 'conversion'
 *     &amp;&amp; context.getProcessedRawEvent().getSourceGeoIps()[0].getCountry().getIsoCode() === 'US'
 * </pre>
 *
 * <h2>Prehandler Actions</h2>
 * <p>
 * The prehandler action should be represented by a procedure. The purpose is to change something in the current
 * {@link com.extole.api.event.ProcessedRawEvent}
 * The Javascript has the access to the <code>context</code> variable which
 * represents the {@link com.extole.api.prehandler.PrehandlerActionContext}. The change of the
 * <code>ProcessedRawEvent</code> is done by accessing it's builder
 * {@link com.extole.api.prehandler.ProcessedRawEventBuilder}.
 * <p>
 * This is a prehandler action example that does some changes:
 *
 * <pre>
 * context.getEventBuilder().withEventName('custom_name');
 * context.getEventBuilder().withSandbox('production-custom');
 * context.getEventBuilder().addData('custom_data', 'custom_value');
 * context.getEventBuilder().removeData('bad_data');
 * context.addLogMessage('Hi from inside prehandler!');
 * </pre>
 */
package com.extole.api.prehandler;
