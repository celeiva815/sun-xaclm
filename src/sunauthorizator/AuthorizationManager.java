/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunauthorizator;

import com.sun.xacml.Indenter;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Tito
 */
public class AuthorizationManager {

    public String getRequestAndPoliciesResponse(File xacmlRequest, List<File> xacmlPolicies) {
        
        PDP pdp = createPDP(xacmlPolicies);
        RequestCtx request = createPEP(xacmlRequest);
        
        if (request != null) {
            
            ResponseCtx response = pdp.evaluate(request);
            Result result = (Result) response.getResults().toArray()[0];
            return getDecision(result);
        }
        
        return "Hubo un error...";
    }
    
    private PDP createPDP(List<File> xacmlPolicies) {
        
        FilePolicyModule policyModule = new FilePolicyModule();
        policyModule.addPolicy(xacmlPolicies.get(0).getPath());
        
        CurrentEnvModule envModule = new CurrentEnvModule();
        
        PolicyFinder policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(policyModule);
        policyFinder.setModules(policyModules);

        AttributeFinder attrFinder = new AttributeFinder();
        List attrModules = new ArrayList();
        attrModules.add(envModule);
        attrFinder.setModules(attrModules);
        
        PDP pdp = new PDP(new PDPConfig(attrFinder, policyFinder, null));
        
        return pdp;
    }
    
    private RequestCtx createPEP(File xacmlRequest) {
        
        try {
            Document xmlRequest = createDocumentFromString(xacmlRequest);
            RequestCtx request = RequestCtx.getInstance(xmlRequest);
        
            return request;
        } catch (ParsingException | ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(AuthorizationManager.class.getName()).log(Level.SEVERE, null, ex);
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
    
    
    
    private Document createDocumentFromString(File xacmlRequest) throws ParserConfigurationException, SAXException, IOException {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            return builder.parse(xacmlRequest);
    }
    
}
