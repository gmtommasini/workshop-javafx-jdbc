package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

		public static Stage currentStage(ActionEvent event) {
			/* getting the stage of the event's source */
			return (Stage)((Node) event.getSource()).getScene().getWindow();
			//event source in Node form
		}
}
