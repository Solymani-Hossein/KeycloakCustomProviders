package solymani.keycloak.custom.protocol.groupmapper;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

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
		configProperties.add(new ProviderConfigProperty(ATTR_KEY, "Attribute key", "Group Attribute Key.", ProviderConfigProperty.STRING_TYPE,""));
		configProperties.add(new ProviderConfigProperty(ATTR_VALUE, "Attribute value", "Group Attribute Value.", ProviderConfigProperty.STRING_TYPE,""));

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
	protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
		int key = Integer.parseInt(mappingModel.getConfig().get(ATTR_KEY));
		int value = Integer.parseInt(mappingModel.getConfig().get(ATTR_VALUE));

		int randomNumber = (int) (Math.random() * (key - value)) + key;

		OIDCAttributeMapperHelper.mapClaim(token, mappingModel, randomNumber);
	}
}
