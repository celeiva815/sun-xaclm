/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunauthorizator;

import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.FilePolicyModule;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
    
/**
 *
 * @author Tito
 */
public class SunAuthorizationManager {

    public String getRequestAndPoliciesResponse(File xacmlRequest, List<File> xacmlPolicies) {
        
        PDP pdp = createPDP(xacmlPolicies);
        RequestCtx request = createPEP(xacmlRequest);
        
        if (request != null) {
            
            ResponseCtx response = pdp.evaluate(request);
            
            String information = "";
             // output to whatever uses the Request
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            response.encode(output);
            
            try {
                return new String(output.toByteArray(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SunAuthorizationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return "Hubo un error...";
    }
    
    private PDP createPDP(List<File> xacmlPolicies) {
        
        //Agregamos los archivos xml con políticas
        FilePolicyModule policyModule = new FilePolicyModule();
        
        for (File policy : xacmlPolicies) {
            policyModule.addPolicy(policy.getPath());
            
        }
        
        //Construimos las políticas 
        PolicyFinder policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(policyModule);
        policyFinder.setModules(policyModules);

        //Creamos la estructura de atributos    
        CurrentEnvModule envModule = new CurrentEnvModule();
        AttributeFinder attrFinder = new AttributeFinder();
        List attrModules = new ArrayList();
        attrModules.add(envModule);
        attrFinder.setModules(attrModules);
        
        //Creamos el PDP con las políticas encontradas y la estructura de atributos
        PDP pdp = new PDP(new PDPConfig(attrFinder, policyFinder, null));
        
        return pdp;
    }
    
    private RequestCtx createPEP(File xacmlRequest) {
        
        try {
            RequestCtx request = RequestCtx.getInstance(new FileInputStream(xacmlRequest));
        
            return request;
        } catch (ParsingException | IOException ex) {
            Logger.getLogger(SunAuthorizationManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return null;
        
    }
    
    
    private String getDecision(Result result) {
        
        switch (result.getDecision()) {
            
            case Result.DECISION_DENY:
                return "Autorización denegada.";
            
            case Result.DECISION_INDETERMINATE:
                return "Autorización indeterminada.";
            
            case Result.DECISION_NOT_APPLICABLE:
                return "Autorización no aplicable.";
                
            case Result.DECISION_PERMIT:
                return "Autorización permitida.";
                
            default:
                return "No se pudo determinar el resultado.";
        }
    }
}
