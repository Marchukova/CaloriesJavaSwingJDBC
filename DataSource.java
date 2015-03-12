import java.sql.Date;
import java.util.ArrayList;


interface DataSource {
	ArrayList<TableRowData> getDataForDate( Date date );
	TableRowData getNutritions( String productName );
	boolean isProductAlreadyIn( String productName );
	void addProduct( Object[] productNutrision );
	void saveDailyConsumption( ArrayList<TableRowData> newData, Date date );
}
