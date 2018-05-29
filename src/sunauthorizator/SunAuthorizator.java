/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunauthorizator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Tito
 */
public class SunAuthorizator extends Application {
    
    private File xacmlRequest;
    private List<File> xacmlPolicies;
    private FileChooser fileChooser;
    private Stage stage;
   
    @FXML
    private Text xacmlRequestText;
    
    @FXML
    private Text xacmlPoliciesText;
    
    @FXML
    private Label informationLabel;
    
    @Override
    public void start(Stage stage) throws IOException {
        
        Parent root = FXMLLoader.load(getClass().getResource("main_view.fxml"));
        Scene scene = new Scene(root);
    
        this.stage = stage;
        this.stage.setTitle("Autorizador Sun XACML");
        this.stage.setScene(scene);
        this.stage.show();
    }
    
    @FXML
    public void selectXACMLRequest(ActionEvent actionEvent) throws IOException{

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XACMLRequest", "*.xml"));
        xacmlRequest  = fileChooser.showOpenDialog(stage);
        xacmlRequestText.setText(xacmlRequest.getName());

    }
    
    @FXML
    public void selectXACMLPolicies(ActionEvent actionEvent) throws IOException{

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XACMLRequest", "*.xml"));
        xacmlPolicies  = fileChooser.showOpenMultipleDialog(stage);
        xacmlPoliciesText.setText("" + xacmlPolicies.size() + " políticas seleccionadas.");
        
    }
    
    @FXML
    public void checkAuthorization(ActionEvent actionEvent) {
        
        //TODO implements the logic
        AuthorizationManager authorizator = new AuthorizationManager();
        String response = authorizator.getRequestAndPoliciesResponse(xacmlRequest, xacmlPolicies);
        
        informationLabel.setText(response);
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    
}
