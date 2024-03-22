package solymani.keycloak.custom.protocol.groupmapper;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import solymani.keycloak.custom.protocol.groupmapper.entity.CustomGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author solymani-hossein
 */
public class CustomGroupMapper extends AbstractOIDCProtocolMapper
		implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

	public static final String PROVIDER_ID = "oidc-custom-group-mapper";

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

	static final String ATTR_KEY = "key";
	static final String ATTR_VALUE = "value";

	static {
		configProperties.add(new ProviderConfigProperty(ATTR_KEY, "Attribute key", "Group Attribute Key.",
				ProviderConfigProperty.STRING_TYPE, ""));

		configProperties.add(new ProviderConfigProperty(ATTR_VALUE, "Attribute value", "Group Attribute Value.",
				ProviderConfigProperty.STRING_TYPE, ""));

		configProperties.add(new ProviderConfigProperty(ATTR_KEY, "Attribute key", "Group Attribute Key.",
				ProviderConfigProperty.MULTIVALUED_LIST_TYPE, ""));

		OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
		OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, CustomGroupMapper.class);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getDisplayCategory() {
		return TOKEN_MAPPER_CATEGORY;
	}

	@Override
	public String getDisplayType() {
		return "Group Membership Filter";
	}

	@Override
	public String getHelpText() {
		return "Map user group membership by group Attribute.";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return configProperties;
	}

	@Override
	protected void setClaim(final IDToken token, final ProtocolMapperModel mappingModel,
			final UserSessionModel userSession,
			final KeycloakSession keycloakSession, final ClientSessionContext clientSessionCtx) {
		final String key = mappingModel.getConfig().get(ATTR_KEY);
		final String value = mappingModel.getConfig().get(ATTR_VALUE);

		mappingModel.getConfig().put(ProtocolMapperUtils.MULTIVALUED, "true");

		var groups = userSession.getUser().getGroupsStream().map(group -> {

			if (group.getAttributes() != null && group.getAttributes().containsKey(key)
					&& group.getAttributes().get(key).contains(value)) {

				return new CustomGroup(group.getId(), group.getName());

			} else {
				return null;
			}
		}).filter(data -> data != null);
		OIDCAttributeMapperHelper.mapClaim(token, mappingModel, groups);
	}

	// private static List<CustomGroup> customGroups = new ArrayList<CustomGroup>();
	// private static void filterGroupsRecursive(final Stream<GroupModel> groups,
	// final String key) {
	// CustomGroup customGroup = new CustomGroup();
	//
	// if (groups != null) {
	//
	// groups.forEach(group -> {
	//
	// if (group.getAttributes() != null && group.getAttributes().containsKey(key))
	// {
	// customGroup.setId(group.getId());
	// customGroup.setName(group.getName());
	// customGroups.add(customGroup);
	//
	// } else {
	// }
	// filterGroupsRecursive(group.getSubGroupsStream(), key);
	// });
	// }
	// }
}
