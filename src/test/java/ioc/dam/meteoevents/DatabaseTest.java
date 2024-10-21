package ioc.dam.meteoevents;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Classe per testejar la Base de dades
 *
 * @author rhospital
 */
public class DatabaseTest {

    private IDatabaseConnection dbUnitConnection;

    /**
     * Configura la connexió a la base de dades abans de cada prova.
     * Inicialitza DbUnit i carrega les dades de prova.
     */
    @BeforeEach
    public void setUp() throws Exception {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/meteoevents";
        String username = "postgres";
        String password = "postgres";

        // Establir connexió amb la base de dades
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        dbUnitConnection = new DatabaseConnection(connection);

        // Carregar les dades de prova des d'un fitxer XML
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/dataset.xml"));

        // Realitzar la configuració inicial de la base de dades
        DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
    }

    /**
     * Prova si les dades s'insereixen correctament a la taula funcional.
     */
    @Test
    public void testInsertFuncional() throws Exception {
        // Comprovar que existeixen les dades
        assertTrue(checkIfRecordExists("funcional", "USR", "Usuari"));
        assertTrue(checkIfRecordExists("funcional", "ADM", "Administrador"));
    }

    /**
     * Prova si les dades s'insereixen correctament a la taula usuaris.
     */
    @Test
    public void testInsertUsuaris() throws Exception {
        // Comprovar que existeixen les dades
        assertTrue(checkIfRecordExists("usuaris", "admin", "Administrador"));
        assertTrue(checkIfRecordExists("usuaris", "convidat", "Convidat"));
    }

    /**
     * Comprova si un registre existeix a la taula especificada.
     *
     * @param tableName nom de la taula
     * @param username   nom d'usuari per buscar
     * @param nomC      nom complet per buscar
     * @return true si el registre existeix, false altrament
     */
    private boolean checkIfRecordExists(String tableName, String username, String nomC) {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE nom_usuari = '%s' AND nom_c = '%s'", tableName, username, nomC);
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/meteoevents", "postgres", "postgres");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retorna true si hi ha almenys un registre
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Retorna false si hi ha algun error o no es troba el registre
    }
}
