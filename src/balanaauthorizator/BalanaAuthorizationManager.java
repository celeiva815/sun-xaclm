/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balanaauthorizator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wso2.balana.Balana;
import org.wso2.balana.ConfigurationStore;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.impl.CurrentEnvModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import sunauthorizator.SunAuthorizationManager;

/**
 *
 * @author Tito
 */
public class BalanaAuthorizationManager {

    public String getRequestAndPoliciesResponse(File xacmlRequest, List<File> xacmlPolicies) {
        
        //Creamos el PDP
        PDP pdp = createPDP(xacmlPolicies);
        AbstractRequestCtx request = createRequest(xacmlRequest);
        
        if (request != null) {
            //Evaluamos el archivo directamente, porque convertirlo en un Node es un
            //problema.
             ResponseCtx response = pdp.evaluate(request);
             return response.encode();
        }
        
        return "Hubo un error...";
    }
    
    private PDP createPDP(List<File> xacmlPolicies) {
        
        System.setProperty(ConfigurationStore.PDP_CONFIG_PROPERTY, "config_rbac.xml");
        
        //Pasamos las políticas a un Set con los path
        Set<String> policies = new HashSet<>();
        xacmlPolicies.forEach((file) -> {
            policies.add(file.getPath());
        });
                
        //Construimos las políticas 
        FileBasedPolicyFinderModule policyModule = new FileBasedPolicyFinderModule(policies);
        PolicyFinder policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(policyModule);
        policyFinder.setModules(policyModules);

        //Creamos la configuración en base a la configuración por default.
        Balana balana = Balana.getInstance();
        PDPConfig config = balana.getPdpConfig();
        PDPConfig pdpConfig = new PDPConfig(config.getAttributeFinder(), policyFinder, config.getResourceFinder());
        
        //Creamos el PDP con las políticas encontradas y la configuración
        PDP pdp = new PDP(pdpConfig);
        
        return pdp;
    }
    
    private AbstractRequestCtx createRequest(File xacmlRequest) {
     
        AbstractRequestCtx request = null;
        ResponseCtx response = null;

        try {
                FileInputStream input = new FileInputStream(xacmlRequest);
                request = RequestCtxFactory.getFactory().getRequestCtx(input);
                
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(BalanaAuthorizationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(BalanaAuthorizationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return request;
    }
}
