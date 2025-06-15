package views;

import connection.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author Marcos
 */

/**
 * Classe responsável pela interface de cadastro de produtos
 * Permite inserir novos produtos e visualizar os produtos existentes
 */
public class TelaCadastroProduto extends JFrame {
    // Componentes da interface gráfica
    private JTextField txtNome, txtCategoria, txtPreco, txtEstoque;
    private JButton btnSalvar, btnRemover; // Adicionar btnRemover
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;

    /**
     * Construtor da classe
     * Inicializa e configura a janela e seus componentes
     */
    public TelaCadastroProduto() {
        // Configuração básica da janela
        setTitle("Cadastro de Produtos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Criação do formulário
        JPanel painelFormulario = criarPainelFormulario();
        add(painelFormulario, BorderLayout.NORTH);

        // Criação da tabela de produtos
        criarTabela();
        add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        // Carrega dados iniciais da tabela
        carregarProdutos();

        setVisible(true);
    }

    /**
     * Cria o painel de formulário com campos para entrada de dados
     * @return JPanel contendo o formulário
     */
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(5, 2, 5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Inicialização dos campos de texto
        txtNome = new JTextField();
        txtCategoria = new JTextField();
        txtPreco = new JTextField();
        txtEstoque = new JTextField();
        btnSalvar = new JButton("Salvar");
        btnRemover = new JButton("Remover"); // Novo botão

        // Adiciona os componentes ao painel
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(new JLabel("Categoria:"));
        painel.add(txtCategoria);
        painel.add(new JLabel("Preço:"));
        painel.add(txtPreco);
        painel.add(new JLabel("Estoque:"));
        painel.add(txtEstoque);
        
        // Criar um painel para os botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnRemover);
        painel.add(painelBotoes);

        // Configura o evento de clique do botão salvar
        btnSalvar.addActionListener(e -> {
            salvarProduto();
            carregarProdutos();
            limparCampos();
        });

        // Configura o evento de clique do botão remover
        btnRemover.addActionListener(e -> removerProdutoSelecionado());

        return painel;
    }

    /**
     * Cria a tabela para exibição dos produtos
     */
    private void criarTabela() {
        modeloTabela = new DefaultTableModel(
            new String[]{"ID", "Nome", "Categoria", "Preço", "Estoque"}, 0
        );
        tabelaProdutos = new JTable(modeloTabela);
    }

    /**
     * Carrega os produtos do banco de dados e exibe na tabela
     */
    private void carregarProdutos() {
        modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar
        try (Connection con = Conexao.getConexao();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Produtos")) {

            // Adiciona cada produto encontrado na tabela
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("categoria"),
                    rs.getDouble("preco"),
                    rs.getInt("estoque")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + ex.getMessage());
        }
    }

    /**
     * Salva um novo produto no banco de dados
     */
    private void salvarProduto() {
        try (Connection con = Conexao.getConexao()) {
            String sql = "INSERT INTO Produtos (Nome, Categoria, Preco, Estoque) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            
            // Prepara os dados para inserção
            stmt.setString(1, txtNome.getText());
            stmt.setString(2, txtCategoria.getText());
            stmt.setDouble(3, Double.parseDouble(txtPreco.getText()));
            stmt.setInt(4, Integer.parseInt(txtEstoque.getText()));
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + ex.getMessage());
        }
    }

    /**
     * Remove o produto selecionado na tabela
     */
    private void removerProdutoSelecionado() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto para remover.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja remover este produto?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try (Connection con = Conexao.getConexao()) {
                int idProduto = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                String sql = "DELETE FROM Produtos WHERE id = ?";
                
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, idProduto);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!");
                carregarProdutos(); // Atualiza a tabela
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao remover produto: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Limpa os campos do formulário após salvar
     */
    private void limparCampos() {
        txtNome.setText("");
        txtCategoria.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
    }
}
