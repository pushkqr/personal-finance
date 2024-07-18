package app;

public class Transaction {
    private int id;
    private int userId;
    private double amount;
    private String category;
    private String type;
    private String date;

    public Transaction(int userId, double amount, String category, String type, String date){
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;

    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
    public double getAmount() {return amount;}
    public void setAmount(double amount) {this.amount = amount;}
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}
}
