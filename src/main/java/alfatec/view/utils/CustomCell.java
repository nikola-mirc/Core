package alfatec.view.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class CustomCell<S, T> extends TableCell<S, T> {

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		setText(empty ? null : getString());
		if (item == null || empty)
			setTooltip(null);
		else {
			Tooltip tooltip = new Tooltip();
			tooltip.setWrapText(true);
			tooltip.textProperty().bind(itemProperty().asString());
			setTooltip(tooltip);
		}
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}
