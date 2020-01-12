package alfatec.controller.utils;

import java.util.List;

public class Utils {

	public static String cyrillicToLatin(String text) {
		char[] abcCyr = { 'а', 'б', 'в', 'г', 'д', 'ђ', 'е', 'ж', 'з', 'и', 'ј', 'к', 'л', 'љ', 'м', 'н', 'њ', 'о', 'п',
				'р', 'с', 'т', 'ћ', 'у', 'ф', 'х', 'ц', 'ч', 'џ', 'ш' };
		String[] abcLat = { "a", "b", "v", "g", "d", "đ", "e", "ž", "z", "i", "j", "k", "l", "lj", "m", "n", "nj", "o",
				"p", "r", "s", "t", "ć", "u", "f", "h", "c", "č", "dž", "š" };
		StringBuilder builder = new StringBuilder();
		outer: for (int i = 0; i < text.length(); i++) {
			for (int x = 0; x < abcCyr.length; x++)
				if (text.charAt(i) == abcCyr[x]) {
					builder.append(abcLat[x]);
					continue outer;
				}
			builder.append(text.charAt(i));
		}
		return builder.toString();
	}

	public static boolean notEmpty(String text) {
		return text != null && text.length() != 0;
	}

	public static boolean equal(String original, String copy) {
		return copy != null && copy.equals(original);
	}

	public static String mergeList(List<String> list) {
		StringBuilder builder = new StringBuilder();
		list.forEach(s -> builder.append(s).append(","));
		if (builder.length() > 0)
			return builder.substring(0, builder.length() - 1);
		return null;
	}
}
