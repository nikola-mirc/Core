package alfatec.controller.utils;

/**
 * Functional interface, will be needed instead of reflection in some generic
 * methods
 * 
 * @author jelena
 */
@FunctionalInterface
public interface ClearPopUp {
	public void clear();
}
