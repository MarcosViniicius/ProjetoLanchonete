package connection;

// Importações necessárias para conexão com banco e logging
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // Parâmetros da conexão com o banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/sistemaLanchonete";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    // Bloco executado na carga da classe: tenta registrar o driver JDBC
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, "Driver MySQL JDBC não encontrado.", e);
        }
    }

    // Retorna uma conexão ativa com o banco de dados
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Fecha somente a conexão
    public static void closeConexao(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, "Erro ao fechar a conexão com o banco de dados.", ex);
        }
    }

    // Fecha conexão e PreparedStatement
    public static void closeConexao(Connection con, java.sql.PreparedStatement stmt) {
        closeConexao(con);
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, "Erro ao fechar o PreparedStatement.", ex);
        }
    }

    // Fecha conexão, PreparedStatement e ResultSet
    public static void closeConexao(Connection con, java.sql.PreparedStatement stmt, java.sql.ResultSet rs) {
        closeConexao(con, stmt);
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, "Erro ao fechar o ResultSet.", ex);
        }
    }
}
