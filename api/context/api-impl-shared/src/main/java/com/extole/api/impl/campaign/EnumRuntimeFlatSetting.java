package com.extole.api.impl.campaign;

import java.util.List;

import com.extole.model.entity.campaign.EnumVariableMember;

public interface EnumRuntimeFlatSetting extends RuntimeFlatSetting {

    List<EnumVariableMember> getEnumVariableMembers();
}
