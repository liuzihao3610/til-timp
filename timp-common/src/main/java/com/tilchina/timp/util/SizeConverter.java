package com.tilchina.timp.util;/*
 * @author XueYuSong
 * @date 2018-06-21 16:02
 */

public enum SizeConverter {
	Arbitrary {
		@Override
		public String convert(float size) {
			while (size > 1024) {
				size /= 1024;
			}
			return String.format(FORMAT_F, size);
		}
	},
	B {
		@Override
		public String convert(float B) {
			return converter(0, B);
		}
	},
	KB {
		@Override
		public String convert(float KB) {
			return converter(1, KB);
		}
	},
	MB {
		@Override
		public String convert(float MB) {
			return converter(2, MB);
		}
	},
	GB {
		@Override
		public String convert(float GB) {
			return converter(3, GB);
		}
	},
	TB {
		@Override
		public String convert(float TB) {
			return converter(4, TB);
		}
	},
	ArbitraryTrim {
		@Override
		public String convert(float size) {
			while (size > 1024) {
				size /= 1024;
			}

			int sizeInt = (int) size;
			boolean isfloat = size - sizeInt > 0.0F;
			if (isfloat) {
				return String.format(FORMAT_F, size);
			}
			return String.format(FORMAT_D, sizeInt);
		}
	},
	BTrim {
		@Override
		public String convert(float B) {
			return trimConverter(0, B);
		}
	},
	KBTrim {
		@Override
		public String convert(float KB) {
			return trimConverter(1, KB);
		}
	},
	MBTrim {
		@Override
		public String convert(float MB) {
			return trimConverter(2, MB);
		}
	},
	GBTrim {
		@Override
		public String convert(float GB) {
			return trimConverter(3, GB);
		}
	},
	TBTrim {
		@Override
		public String convert(float TB) {
			return trimConverter(4, TB);
		}
	};

	abstract public String convert(float size);

	private static final String[] UNITS = new String[] {
			"B", "KB", "MB", "GB", "TB", "PB", "**"
	};

	private static final int LAST_IDX = UNITS.length-1;

	private static final String FORMAT_F = "%1$-1.2f";
	private static final String FORMAT_F_UNIT = "%1$-1.2f%2$s";

	private static final String FORMAT_D = "%1$-1d";
	private static final String FORMAT_D_UNIT = "%1$-1d%2$s";

	private static String converter(int unit, float size) {
		int unitIdx = unit;
		while (size > 1024) {
			unitIdx++;
			size /= 1024;
		}
		int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
		return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
	}

	private static String trimConverter(int unit, float size) {
		int unitIdx = unit;
		while (size > 1024) {
			unitIdx++;
			size /= 1024;
		}

		int sizeInt = (int) size;
		boolean isfloat = size - sizeInt > 0.0F;
		int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
		if (isfloat) {
			return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
		}
		return String.format(FORMAT_D_UNIT, sizeInt, UNITS[idx]);
	}

	public static String convertBytes(float B, boolean trim) {
		return trim ? trimConvert(0, B, true) : convert(0, B, true);
	}

	public static String convertKB(float KB, boolean trim) {
		return trim ? trimConvert(1, KB, true) : convert(1, KB, true);
	}

	public static String convertMB(float MB, boolean trim) {
		return trim ? trimConvert(2, MB, true) : convert(2, MB, true);
	}

	private static String convert(int unit, float size, boolean withUnit) {
		int unitIdx = unit;
		while (size > 1024) {
			unitIdx++;
			size /= 1024;
		}
		if (withUnit) {
			int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
			return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
		}
		return String.format(FORMAT_F, size);
	}

	private static String trimConvert(int unit, float size, boolean withUnit) {
		int unitIdx = unit;
		while (size > 1024) {
			unitIdx++;
			size /= 1024;
		}

		int sizeInt = (int) size;
		boolean isfloat = size - sizeInt > 0.0F;
		if (withUnit) {
			int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
			if (isfloat) {
				return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
			}
			return String.format(FORMAT_D_UNIT, sizeInt, UNITS[idx]);
		}

		if (isfloat) {
			return String.format(FORMAT_F, size);
		}
		return String.format(FORMAT_D, sizeInt);
	}
}
