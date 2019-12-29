package alfatec.view.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class Utility {

	public static <S, T> void setUpStringCell(TableView<T> table) {
		Callback<TableColumn<T, S>, TableCell<T, S>> stringCellFactory = new Callback<TableColumn<T, S>, TableCell<T, S>>() {
			@Override
			public TableCell<T, S> call(TableColumn<T, S> p) {
				CustomCell<T, S> cell = new CustomCell<T, S>();
				return cell;
			}
		};
		for (Object object : table.getColumns()) {
			@SuppressWarnings("unchecked")
			TableColumn<T, S> column = (TableColumn<T, S>) object;
			column.setCellFactory(stringCellFactory);
		}

	}
}
