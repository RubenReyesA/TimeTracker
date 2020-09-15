package ds_fites1_2;


public class IDsGenerator {

	private static IDsGenerator theInstance;
	/**
	 * Constructor privat per implementar Singleton
	 */
	private IDsGenerator(final int index) {
        this.id = index;
	}
	
	public static IDsGenerator Instance(final int id) {
		if (IDsGenerator.theInstance ==null) {
            IDsGenerator.theInstance = new IDsGenerator(id);
		}
		return IDsGenerator.theInstance;
	}

	private int id;

	public int getId() {
        id++ ;
		return this.id;
	}

	public int getIDNotModified(){
		return this.id;
	}
	
}
