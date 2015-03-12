import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/* This class is used, when DataBase connection if failed,
 * just to show how program works.
 * */
public class temporaryDataSource implements DataSource {
	HashMap<String, Elements> nutrition = new HashMap<String, Elements>();
	HashMap<NewDate, LinkedList<Elements>> consumption = new HashMap<NewDate, LinkedList<Elements>>();
	private final int ARRAY_LIST_CAPACITY;
	private final int PRODUCT_NAME = 0;
	private final int WEIGHT = 1;
	private final int PROTEINS = 0;
	
	private class NewDate extends Date {
		private static final long serialVersionUID = -2619710563228030621L;

		@SuppressWarnings("deprecation")
		public NewDate( Date date ) {
			super(date.getYear(), date.getMonth(), date.getDate());
		}		
	}
	private class Elements {
		Object[] elems;
		Elements( float a, float b, float c, float d ) {
			elems =  new Object[4];
			elems[0] = a;
			elems[1] = b;
			elems[2] = c;
			elems[3] = d;
		}
		Elements( String s, float w ) {
			elems =  new Object[2];
			elems[PRODUCT_NAME] = s;
			elems[WEIGHT] = w;
		}
	}
	public temporaryDataSource( Date date, int capacity ) {
		ARRAY_LIST_CAPACITY = capacity;
		initNutrition();
		initConsumption(date);
	}
	private void initNutrition() {
		nutrition.put("������", new Elements(4.5f,	 2.3f,	 25.0f,	 132f));
		nutrition.put("�����", new Elements(10.3f,	 1.0f,	 73.3f,	 328f));
		nutrition.put("�������", new Elements(11.0f, 	 6.1f,	 65.4f,	 303f));
		nutrition.put("���", new Elements(6.7f,	 0.7f,	 78.9f,	 344f));
		nutrition.put("������������ �����", new Elements(0.0f, 99.9f,	 0.0f, 899f));
		nutrition.put("��������� �����", new Elements(0.5f, 82.5f, 0.8f, 748f));
		nutrition.put("��������� �����", new Elements(0.0f, 99.8f, 0.0f, 898f));
		nutrition.put("������� �������� ������ � �����", new Elements(2.8f, 2.2f, 13.0f, 83f));
		nutrition.put("������� �������� ��������", new Elements(3.9f, 3.2f,	13.6f, 98f));
		nutrition.put("������� �������� ������", new Elements(3.9f, 3.2f, 13.6f, 98f));
		nutrition.put("������� ��������� ��������",	new Elements(5.0f, 4.5f, 15.5f, 123f));
		nutrition.put("������� ��������� ������", new Elements(4.2f, 4.5f, 11.4f, 102f));
		nutrition.put("������� ��������� �����", new Elements(5.0f, 4.5f, 15.5f, 123f));
		nutrition.put("������� ��������� ����� � �����", new Elements(4.8f, 4.5f, 14.7f, 119f));
		nutrition.put("������� ��������� �����������", new Elements(5.9f, 4.5f, 3.7f, 79f));
		nutrition.put("������� ��������� ������ � �����", new Elements(4.9f, 4.3f, 13.7f, 113f));
		nutrition.put("������� ��������� ���������", new Elements(4.7f, 4.2f, 14.4f, 114f));
		nutrition.put("������� ���� �����", new Elements(2.8f, 1.6f, 12.3f, 74f));
		nutrition.put("������� ���� �����",	new Elements(2.8f, 1.6f, 11.0f, 70f));
		nutrition.put("������� ���� ������ � ������", new Elements(2.8f, 1.6f, 11.0f, 70f));
		nutrition.put("������� ���� �����",	new Elements(2.8f, 1.6f, 12.0f, 73f));
		nutrition.put("������� ���� ������", new Elements(2.8f, 1.6f, 11.0f, 70f));
		nutrition.put("���������� 0.1%", new Elements(3.0f, 0.1f, 3.9f, 31f));
		nutrition.put("���������� 1%",	new Elements(3.0f, 1.0f, 4.0f, 40f));
		nutrition.put("���������� 3.2%", new Elements(2.8f, 3.2f, 3.8f, 57f));
		nutrition.put("���������� 3.2% �������", new Elements(2.8f, 3.2f, 8.6f, 77f));
		nutrition.put("���������� ������������ 2.5%", new Elements(2.8f, 2.5f, 4.2f, 51f));
		nutrition.put("��������� ������������ 2.5%", new Elements(3.2f, 2.5f, 8.9f, 71f));
		nutrition.put("����� 0%", new Elements(3.0f, 0.0f, 3.8f, 30f));
		nutrition.put("����� 1%", new Elements(2.8f, 1.0f, 4.0f, 40f));
		nutrition.put("����� 1.5%", new Elements(3.3f, 1.5f, 3.6f, 41f));
		nutrition.put("����� 2%", new Elements(3.4f, 2.0f, 4.7f, 51f));
		nutrition.put("����� 2.5%", new Elements(2.8f, 2.5f, 3.9f, 50f));
		nutrition.put("����� 3.2%",	new Elements(2.8f, 3.2f, 4.1f, 56f));
		nutrition.put("������ 0.5%", new Elements(2.8f, 0.5f, 4.9f, 35f));
		nutrition.put("������ 0.7%", new Elements(3.3f, 0.7f, 4.7f, 38f));
		nutrition.put("������ 1%", new Elements(3.3f, 1.0f, 4.8f, 41f));
		nutrition.put("������ 1.5%", new Elements(2.8f, 1.5f, 4.7f, 44f));
		nutrition.put("������ 2.5%", new Elements(2.8f, 2.5f, 4.7f, 52f));
		nutrition.put("������ 3.2%", new Elements(2.9f, 3.2f, 4.7f, 59f));
		nutrition.put("������ 3.6%", new Elements(2.8f, 3.6f, 4.7f, 62f));
		nutrition.put("������ 4.5%", new Elements(3.1f, 4.5f, 4.7f, 72f));
		nutrition.put("������� 1%", new Elements(3.0f, 1.0f, 4.2f, 40f));
		nutrition.put("������� 2.5%", new Elements(2.9f, 2.5f, 4.2f, 54f));
		nutrition.put("������� 3.2%", new Elements(2.9f, 3.2f, 4.1f, 57f));
		nutrition.put("������� 4%", new Elements(2.8f, 4.0f, 4.2f, 67f));
		nutrition.put("������� 6%", new Elements(5.0f, 6.0f, 4.1f, 84f));
		nutrition.put("��������", new Elements(11.5f, 1.9f, 71.8f, 350f));
		nutrition.put("��������", new Elements(18.6f, 16.0f, 0.2f, 218f));
		nutrition.put("������", new Elements(16.0f, 14.0f, 0.3f, 190f));
		nutrition.put("������� ������", new Elements(23.6f, 1.9f, 0.4f, 113f));
		nutrition.put("������� �������", new Elements(18.2f, 4.2f, 0.6f, 114f));
		nutrition.put("������� ������", new Elements(19.1f, 6.3f, 0.6f, 136f));
		nutrition.put("�������", new Elements(16.1f, 22.8f, 0.7f, 267f));
		nutrition.put("��������", new Elements(3.0f, 0.4f, 5.2f, 28f));
		nutrition.put("������� �������", new Elements(2.5f, 0.3f, 5.4f, 30f));
		nutrition.put("���������", new Elements(2.0f, 0.4f, 18.1f, 80f));
		nutrition.put("������", new Elements(0.8f, 0.1f, 3.0f, 15f));
		nutrition.put("�������", new Elements(0.6f, 0.2f, 4.2f, 20f));
		nutrition.put("������ 0%", new Elements(16.5f, 0.0f, 1.3f, 71f));
		nutrition.put("������ 0.1%", new Elements(16.7f, 0.1f, 2.0f, 76f));
		nutrition.put("������ 0.2%", new Elements(18.0f, 0.2f, 1.8f, 81f));
		nutrition.put("������ 0.3%", new Elements(18.0f, 0.3f, 3.3f, 90f));
		nutrition.put("������ 0.6%", new Elements(18.0f,	 0.6f, 1.8f, 88f));
		nutrition.put("������ 1%", new Elements(16.3f, 1.0f, 1.3f, 79f));
		nutrition.put("������ 1.8%", new Elements(18.0f, 1.8f, 3.3f, 101f));
		nutrition.put("������ 11%", new Elements(16.0f, 11.0f, 1.0f, 170f));
		nutrition.put("������ 18%", new Elements(14.0f, 18.0f, 2.8f, 232f));
		nutrition.put("������ 2%", new Elements(18.0f, 2.0f, 3.3f, 103f));
		nutrition.put("������ 4%", new Elements(15.7f, 4.0f, 1.4f, 104f));
		nutrition.put("������ 5%", new Elements(17.2f, 5.0f, 1.8f, 121f));
		nutrition.put("������ 8%", new Elements(15.0f, 8.0f, 1.5f, 138f));
		nutrition.put("������ 9%", new Elements(16.7f, 9.0f, 2.0f, 159f));
		nutrition.put("������", new Elements(1.5f, 0.1f, 21.8f, 89f));
		nutrition.put("������", new Elements(0.4f, 0.4f, 9.8f, 47f));
		nutrition.put("���� �������", new Elements(12.7f, 10.9f, 0.7f, 157f));
                                             

	}
	private void initConsumption( Date date ) {
		long oneDay = Date.valueOf("2014-05-02").getTime() - Date.valueOf("2014-05-01").getTime();
		LinkedList<Elements> l;
		NewDate newDate = new NewDate(date); 
		l = new LinkedList<Elements>();
		l.add(new Elements("������ 0.5%", 158f));
		l.add(new Elements("������� �������� ��������", 528f));
		l.add(new Elements("������ 3.6%", 18f));
		l.add(new Elements("������� ��������� ������ � �����", 128f));
		l.add(new Elements("������ 18%", 172f));
		consumption.put(newDate, l);
		l = new LinkedList<Elements>();
		NewDate prevDay = new NewDate(new Date(date.getTime() - oneDay));
		l.add(new Elements("������� �������", 158f));
		l.add(new Elements("��������� �����", 528f));
		l.add(new Elements("������ 1.8%", 18f));
		l.add(new Elements("������� 4%", 172f));
		consumption.put(prevDay, l);
		l = new LinkedList<Elements>();
		NewDate nextDay = new NewDate(new Date(date.getTime() + oneDay));
		l.add(new Elements("��������� �����", 158f));
		l.add(new Elements("������", 528f));
		l.add(new Elements("������ 1.8%", 18f));
		l.add(new Elements("������ 4%", 172f));
		consumption.put(nextDay, l);
	}
	
	@Override
	public ArrayList<TableRowData> getDataForDate( Date date ) {
		ArrayList<TableRowData> data = new ArrayList<TableRowData>(ARRAY_LIST_CAPACITY);
		LinkedList<Elements> dayConsumption;
		
		dayConsumption = consumption.get(new NewDate(date));
		if (dayConsumption != null) {
			for (Elements e : dayConsumption) {
				Elements nutrients = nutrition.get(e.elems[PRODUCT_NAME]);
				if (nutrients != null) {
					TableRowData newRow = new TableRowData();
					newRow.set(TableRowData.PRODUCT_INDEX, e.elems[PRODUCT_NAME]);
					newRow.set(TableRowData.WEIGHT_INDEX, e.elems[WEIGHT]);
					for (int i = TableRowData.PROTEINS_INDEX, j = PROTEINS; 
							i <= TableRowData.CALORIES_INDEX; i++, j++)
						newRow.set(i, nutrients.elems[j]);
					data.add(newRow);
				}
			}
		}
		return data;
	}

	@Override
	public TableRowData getNutritions( String productName ) {
		Elements nutrients = nutrition.get(productName);
		TableRowData newRow = new TableRowData();
		
		newRow.set(TableRowData.PRODUCT_INDEX, productName);
		if (nutrients != null) {
			for (int i = TableRowData.PROTEINS_INDEX, j = PROTEINS; 
					i <= TableRowData.CALORIES_INDEX; i++, j++)
				newRow.set(i, nutrients.elems[j]);
		}
		return newRow;
	}

	@Override
	public boolean isProductAlreadyIn( String productName ) {
		return nutrition.containsKey(productName);
	}

	@Override
	public void addProduct( Object[] productNutrition ) {
		nutrition.put((String) productNutrition[0], new Elements((float)productNutrition[1], (float)productNutrition[2], 
				(float)productNutrition[3], (float)productNutrition[4]));
	}
	
	@Override
	public void saveDailyConsumption( ArrayList<TableRowData> newData, Date date ) {
		LinkedList<Elements> l = new LinkedList<Elements>();
		for (int i = 0; i < newData.size(); i++) {
			TableRowData row = newData.get(i);
			if (row.get(TableRowData.PRODUCT_INDEX) == null ||
					row.get(TableRowData.WEIGHT_INDEX) == null ||
						"".equals((String)row.get(TableRowData.PRODUCT_INDEX)) ||
							row.get(TableRowData.PROTEINS_INDEX) == null)
				newData.remove(i);
			else 
				l.add(new Elements((String)row.get(TableRowData.PRODUCT_INDEX), 
						(float)row.get(TableRowData.PROTEINS_INDEX)));
		}
		consumption.remove(date);
		consumption.put(new NewDate(date), l);
	}
				
}
