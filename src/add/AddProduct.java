package add;

import main.Shop;
import shop.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AddProduct extends JFrame {

    int whichCategory;
    private JPanel mainPanel, productPanel;
    private AddTextLabel addName, addMass, addPrice;
    private GridLayout layout;
    private JButton save;
    private String name, massS, priceS;
    private float mass, price;
    private JList list;
    private DefaultListModel model;
    private JLabel label;
    private ArrayList<Product> products;

    public AddProduct(int category,  JList list, DefaultListModel model, ArrayList products, JLabel label){
        this.whichCategory = category;
        this.list = list;
        this.model = model;
        this.products = products;
        this.label = label;
        this.setSize(400, 150);
        setMainPanel();
        setProductPanel();
        panelElements();
        addButton();
    }

    // panel całego okna
    public void setMainPanel(){
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        setTitle("Dodaj produkt");
        mainPanel.setLayout(new BorderLayout());
    }

    // panel do którego dodaję obiekty klasy AddTextLabel
    public void setProductPanel(){
        productPanel = new JPanel();
        layout = new GridLayout(1, 3);
        productPanel.setLayout(layout);
        mainPanel.add(productPanel, BorderLayout.CENTER);
    }

    public void panelElements(){
       setAddName();
       setAddMass();
       setAddPrice();
    }

    public void setAddName(){
        addName = new AddTextLabel("Nazwa");
        addName.getTextField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        productPanel.add(addName.getPanel());
    }

    public void setAddMass(){
        addMass = new AddTextLabel("Masa");
        addMass.getTextField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        productPanel.add(addMass.getPanel());
    }

    public void setAddPrice(){
        addPrice = new AddTextLabel("Cena");
        addPrice.getTextField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        productPanel.add(addPrice.getPanel());
    }

    public void addProduct(){
        getFields();
        if(!name.equals("") && !massS.equals("") && !priceS.equals("")) {
            try {
                toFloat();
                setList();
                setProducts();
                label.setText("Dodano produkt " + name);
                dispose();
            } catch (NumberFormatException exception){
                label.setText("Podano zły format masy i/lub ceny");
            }
        } else {
            label.setText("Podaj potrzebne parametry produktu!");
        }
    }

    public void addButton(){
        save = new JButton("Zapisz");
        mainPanel.add(save, BorderLayout.SOUTH);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
    }

    // metoda pobierająca zawartość JTextFieldów
    public void getFields(){
        name = addName.getTextField().getText();
        massS = addMass.getTextField().getText();
        priceS = addPrice.getTextField().getText();
        //toFloat();
    }

    // metoda zamieniająca Stringi na floaty
    public void toFloat(){
        mass = Float.parseFloat(massS);
        price = Float.parseFloat(priceS);
    }

    // metoda aktualizująca listę produktów
    public void setList(){
        Shop shop = new Shop();
        shop.addProducts(whichCategory, name, mass, price);
        model.addElement(name);
        list.setModel(model);
    }

    // metoda aktualizująca ArrayList<Products>
    public void setProducts(){
        Product product = new Product(name, mass, price);
        products.add(product);
        for(int i = 0; i < products.size(); i++)
            System.out.println(products.get(i).getName());
    }

}
