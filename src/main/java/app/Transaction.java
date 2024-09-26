package app;

public class Transaction {
    private int id;
    private final int userId;
    private final double amount;
    private final String category;
    private final String type;
    private final String date;
    private final int category_id;
    private final int type_id;
    private String note;

    public Transaction(int userId, double amount, String category, String type, String date, int category_id, int type_id, String note){
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
        this.category_id = category_id;
        this.type_id = type_id;
        this.note = note;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getUserId() {return userId;}
    public double getAmount() {return amount;}
    public String getCategory() {return category;}
    public String getType() {return type;}
    public String getDate() {return date;}
    public int getCategoryID() {return category_id;}
    public int getTypeID() {return type_id;}
    public String getNote() {return note;}

}
