package com.falcon.falcon.services.impl;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;

@Service
public class OpenVPNServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(OpenVPNServiceImpl.class);

    @Value("${openvpn.server.url}")
    private String openvpnServerUrl;

    @Value("${openvpn.admin.username}")
    private String adminUsername;

    @Value("${openvpn.admin.password}")
    private String adminPassword;

    private XmlRpcClient getXmlRpcClient() throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        String rpcUrl = openvpnServerUrl.endsWith("/RPC2") ? openvpnServerUrl : openvpnServerUrl + "/RPC2";
        config.setServerURL(URI.create(rpcUrl).toURL());
        config.setBasicUserName(adminUsername);
        config.setBasicPassword(adminPassword);
        config.setEnabledForExtensions(true);
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

    public byte[] generateUserConfig(String username) throws Exception {
        logger.info("Generating config for user: {}", username);
        try {
            XmlRpcClient client = getXmlRpcClient();
            Object[] params = new Object[]{username, true};
            String profile = (String) client.execute("GetUserlogin", params);
            return profile.getBytes();
        } catch (Exception e) {
            logger.error("Error generating user config via XML-RPC: ", e);
            throw e;
        }
    }
}
