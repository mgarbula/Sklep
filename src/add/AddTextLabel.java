package add;

import javax.swing.*;

// klasa dziedzicząca po JPanel. ma pola JTextField i JLabel
// używam jej w oknie do dodawania produktów (klasa AddProduct)

public class AddTextLabel extends JPanel {

    private JPanel panel;
    private JTextField textField;
    private JLabel label;
    private SpringLayout layout;

    public AddTextLabel(String string){
        setPanel();
        addLabel(string);
        addTextField();
    }

    public void setPanel(){
        panel = new JPanel();
        layout = new SpringLayout();
        panel.setLayout(layout);
    }

    public void addLabel(String string){
        label = new JLabel(string, SwingConstants.CENTER);
        // ustawiam label (najpierw lewa, potem górna krawędz)
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, panel);
        panel.add(label);
    }

    public void addTextField(){
        textField = new JTextField();
        textField.setColumns(12);
        // ustawiam textField
        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.SOUTH, label);
        panel.add(textField);
    }

    public JTextField getTextField() {
        return textField;
    }

    // metoda zwracająca JComponent, jest potrzebna abym mógł używać mojej klasy, czyli dodawac Label i TextField do AddProduct
    public JComponent getPanel(){
        return panel;
    }

}
