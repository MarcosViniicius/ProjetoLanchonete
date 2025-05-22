package views;
import dao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaCadastroProduto extends JFrame {
    private JTextField txtNome, txtCategoria, txtPreco, txtEstoque;
    private JButton btnSalvar;

    public TelaCadastroProduto() {
        setTitle("Cadastro de Produtos");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5,2));

        add(new JLabel("Nome:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("Categoria:"));
        txtCategoria = new JTextField();
        add(txtCategoria);

        add(new JLabel("Preço:"));
        txtPreco = new JTextField();
        add(txtPreco);

        add(new JLabel("Estoque:"));
        txtEstoque = new JTextField();
        add(txtEstoque);

        btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        btnSalvar.addActionListener(e -> salvarProduto());
        setVisible(true);
    }

    private void salvarProduto() {
        try (Connection con = Conexao.getConexao()) {
            String sql = "INSERT INTO Produtos (Nome, Categoria, Preco, Estoque) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, txtNome.getText());
            stmt.setString(2, txtCategoria.getText());
            stmt.setDouble(3, Double.parseDouble(txtPreco.getText()));
            stmt.setInt(4, Integer.parseInt(txtEstoque.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Produto cadastrado!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}