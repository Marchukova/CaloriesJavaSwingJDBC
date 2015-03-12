
/* Class representing data of one row in table */
class TableRowData {
	State state;
	private Object[] data = {null, null, null, null, null, null};
	public final static int PRODUCT_INDEX = 0;
	public final static int WEIGHT_INDEX = 1;
	public final static int PROTEINS_INDEX = 2;
	public final static int FATS_INDEX = 3;
	public final static int CARBOHYDRATE_INDEX = 4;
	public final static int CALORIES_INDEX = 5;

	enum State {NOT_CHANGED, PRODUCT_CHANGED, WEIGHT_CHANGED, ADDED};
	public TableRowData() {
		state = State.NOT_CHANGED;
	}
	Object get( int i ) {
		return data[i];
	}
	void set( int i, Object value ) {
		data[i] = value;
	}
	void setState( State state ) {
		if (this.state != State.ADDED && this.state != State.PRODUCT_CHANGED)
			this.state = state;
	}
}
