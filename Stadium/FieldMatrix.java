public class FieldMatrix {

		SectorT [][] field;
		int foulTerritory;

		public FieldMatrix (int max, int foulSize) {
			foulTerritory = foulSize;
			field = new SectorT [max+foulSize][max+foulSize];

			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field.length; j++) {
					field[i][j] = SectorT.NONE;
				}
			}

		}

		//gets the sector corresponding the given sector
		public SectorT get (int x, int y) {
			try {
				return field[y+foulTerritory][x+foulTerritory];
			} catch (Exception e) {
				return SectorT.NONE;
			}
		}

		public void set (int x, int y, SectorT toSet) {
			field[y+foulTerritory][x+foulTerritory] = toSet;
		}
		
		public int size () {
			return field.length;
		}

		public void print () {

			for (int i = field.length-1; i >= 0; i--) {

				System.out.print(i);

				for (int j = 0; j < field.length; j++) {

					if (field[i][j] == SectorT.INPLAY) {
						System.out.print("@");
					}
					
					else if (field[i][j] == SectorT.FOUL) {
						System.out.print("F");
					}
					
					else {
						System.out.print(" ");
					}

				}

				System.out.println();

			}

		}

	}