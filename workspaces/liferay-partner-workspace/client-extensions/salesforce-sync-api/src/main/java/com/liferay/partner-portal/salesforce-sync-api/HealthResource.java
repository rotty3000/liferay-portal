package com.liferay.partner.portal.salesforce.sync.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sforce.async.*;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@RequestMapping("/")
@RestController
public class HealthResource {

  private BulkConnection getBulkConnection(String userName, String password)
      throws ConnectionException, AsyncApiException {
    String securityToken = "u1lhs1bvtHKk12auulldx1c7";
    String apiVersion = "56.0";

    ConnectorConfig partnerConfig = new ConnectorConfig();

    partnerConfig.setUsername(userName);
    partnerConfig.setPassword(password + securityToken);
    partnerConfig.setAuthEndpoint("https://test.salesforce.com/services/Soap/u/" + apiVersion);

    new PartnerConnection(partnerConfig);

    ConnectorConfig config = new ConnectorConfig();
    config.setSessionId(partnerConfig.getSessionId());

    String soapEndpoint = partnerConfig.getServiceEndpoint();
    String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/"))
        + "async/" + apiVersion;

    config.setRestEndpoint(restEndpoint);
    config.setCompression(true);
    config.setTraceMessage(false);
  
    BulkConnection connection = new BulkConnection(config);

    return connection;
  }

  @GetMapping("/")
  public String ready(@AuthenticationPrincipal Jwt jwt) throws Exception {
    System.out.println("============================\n");
    System.out.println("JWT: " + jwt);
    System.out.println("\n============================");

    BulkConnection connection = getBulkConnection("jair.medeiros@liferay.com.prm", "U*V!q6hd7DX@FrR6Dfxo");
    
    System.out.println("============================\n");
    System.out.println("Salesforce BulkConnection: " + connection);
    System.out.println("\n============================");

    return "READY";
  }
}
