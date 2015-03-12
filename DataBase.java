import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;

/* Work with database */
public class DataBase implements DataSource {
	private ArrayList<TableRowData> data;
	private ArrayList<Id> consumptionIds; //Is used to find out deleted rows
	private String URL = "jdbc:mysql://localhost:3306/Calories";
	private String name = "root", password = "Password";
	private final int capacity;
	
	/* Contains consumptions id and object connected to this id */
	private class Id{
		int id;
		TableRowData consumption;
		
		Id( int id,	TableRowData consumption ) {
			this.id = id;
			this.consumption = consumption;
		}
		@Override
		public boolean equals( Object o ) {
			if (consumption == ((Id)o).consumption)
				return true;
			return false;
		}
	}
	
	public DataBase( int capacity ) throws SQLException, ClassNotFoundException {
		this.capacity = capacity;
		Class.forName("com.mysql.jdbc.Driver");
		// Check if it possible to create connection
		try (Connection c = DriverManager.getConnection(URL, name, password);) {
		}	
	}
		
	public ArrayList<TableRowData> getDataForDate( Date date ) {
		//make empty data
		data = new ArrayList<TableRowData>(capacity);		
		consumptionIds = new ArrayList<Id>(capacity);		
		try (Connection c = DriverManager.getConnection(URL, name, password);) {
			PreparedStatement st = c.prepareStatement("SELECT ProductName, Weight, Proteins, Fats, "
					+ "Carbohydrate, Calories, id FROM Nutrition AS N, Consumption AS A "
					+ "WHERE `Date` = ? AND A.idProduct = N.idProduct");
			st.setDate(1, date);
			ResultSet prs = st.executeQuery();
			while (prs.next()) {
				TableRowData newRow = new TableRowData();
				newRow.set(0, new String(prs.getString("ProductName").getBytes("latin1"), "cp1251"));
				for (int i = TableRowData.WEIGHT_INDEX; i <= TableRowData.CALORIES_INDEX; i++) 
					newRow.set(i, prs.getFloat(i + 1));
				data.add(newRow);
				consumptionIds.add(new Id(prs.getInt("id"), newRow));
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public TableRowData getNutritions( String productName ) {
		TableRowData row = null;
		try (Connection c = DriverManager.getConnection(URL, name, password);) {
			PreparedStatement st = c.prepareStatement("SELECT ProductName, Proteins, Fats, Carbohydrate, "
					+ "Calories FROM Nutrition WHERE ProductName = ?");
			st.setString(1, new String(productName.getBytes("cp1251"), "latin1"));
			ResultSet prs = st.executeQuery();
			while (prs.next()) {
				row = new TableRowData();
				row.set(TableRowData.PRODUCT_INDEX, productName);
				for (int i = TableRowData.PROTEINS_INDEX; i <= TableRowData.CALORIES_INDEX; i++) 
					row.set(i, prs.getFloat(i));
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return row;
	}
	
	public boolean isProductAlreadyIn( String productName ) {
		try (Connection c = DriverManager.getConnection(URL, name, password);) {
			PreparedStatement st = c.prepareStatement("SELECT ProductName FROM Nutrition WHERE "
					+ "ProductName = ?");
			st.setString(1, new String(productName.getBytes("cp1251"), "latin1"));
			ResultSet prs = st.executeQuery();
			if (prs.next()) {
				return true;
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addProduct( Object[] productNutrision ) {
		try (Connection c = DriverManager.getConnection(URL, name, password);) {
			PreparedStatement st = c.prepareStatement("SELECT idProduct FROM Nutrition WHERE ProductName = ?");
			st.setString(1, new String(((String)productNutrision[0]).getBytes("cp1251"), "latin1"));
			ResultSet prs = st.executeQuery();
			st = c.prepareStatement("REPLACE Nutrition VALUES (NULL, ?, ?, ?, ?, ?)");				
			int productNameIndex = 1;
			final int floatColumnsCount = 4;
			if (prs.next()) {
				st = c.prepareStatement("REPLACE Nutrition VALUES (?, ?, ?, ?, ?, ?)");				
				st.setInt(1, prs.getInt("idProduct"));
				productNameIndex++;
			}
			st.setString(productNameIndex, new String(((String)productNutrision[0]).
					getBytes("cp1251"), "latin1"));
			for (int i = productNameIndex + 1; i <= floatColumnsCount + productNameIndex; i++)
				st.setFloat(i, (Float)productNutrision[i - productNameIndex]);
			st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void saveDailyConsumption( ArrayList<TableRowData> newData, Date date ) {
		try (Connection c = DriverManager.getConnection(URL, name, password);) {
			PreparedStatement st;
			for (int i = newData.size() - 1; i >= 0; i--) {
				TableRowData currentRow = newData.get(i);  
				if (currentRow.get(TableRowData.PRODUCT_INDEX) == null ||
						currentRow.get(TableRowData.WEIGHT_INDEX) == null ||
						"".equals((String)currentRow.get(TableRowData.PRODUCT_INDEX)))
					/* Don't save row with no information for product or weight */
					continue;
				switch (currentRow.state) {
				case ADDED:
					st = c.prepareStatement("SELECT idProduct FROM Nutrition WHERE ProductName = ?");
					st.setString(1, new String(((String)currentRow.get(TableRowData.PRODUCT_INDEX)).
							getBytes("cp1251"), "latin1"));
					ResultSet prs = st.executeQuery();
					if (!prs.next())
						/* No product with such name in database */
						continue;
					st = c.prepareStatement("INSERT INTO Consumption VALUES (NULL, ?, ?, ?)");
					/* Set product id */
					st.setInt(1, prs.getInt("idProduct"));
					/* Set product weight */
					st.setFloat(2, (Float)currentRow.get(TableRowData.WEIGHT_INDEX));
					/* Set product date */
					st.setDate(3, date);
					st.execute();
					break;
				case NOT_CHANGED:
					consumptionIds.remove(new Id(-1, currentRow));
					break;
				case PRODUCT_CHANGED:
					st = c.prepareStatement("SELECT idProduct FROM Nutrition WHERE ProductName = ?");
					st.setString(1, new String(((String)currentRow.get(TableRowData.PRODUCT_INDEX)).
							getBytes("cp1251"), "latin1"));
					prs = st.executeQuery();
					if (!prs.next()) {
						/* No product with such name in database */
						consumptionIds.remove(new Id(-1, currentRow));
						continue;
					}
					st = c.prepareStatement("UPDATE Consumption SET idProduct = ?, Weight = ? WHERE id = ?");
					st.setInt(1, prs.getInt("idProduct"));
					st.setFloat(2, (float)currentRow.get(TableRowData.WEIGHT_INDEX));
					st.setInt(3, consumptionIds.get(i).id);
					st.execute();
					consumptionIds.remove(new Id(-1, currentRow));
					break;
				case WEIGHT_CHANGED:
					st = c.prepareStatement("UPDATE Consumption SET Weight = ? WHERE id = ?");
					st.setFloat(1, (float)currentRow.get(TableRowData.WEIGHT_INDEX));
					st.setInt(2, consumptionIds.get(i).id);
					st.execute();
					consumptionIds.remove(new Id(-1, currentRow));
					break;				
				}
			}
			/* Delete deleted rows */
			for (int i = 0; i < consumptionIds.size(); i++) {
				st = c.prepareStatement("DELETE FROM Consumption WHERE id = ?");
				st.setInt(1, consumptionIds.get(i).id);
				st.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
