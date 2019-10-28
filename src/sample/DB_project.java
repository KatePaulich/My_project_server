package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.time.LocalDate;

/**
 * DB_project class for work with the database
 */
public class DB_project {
    /**
     * user a database
     */
    private final String user = "root";
    /**
     * url a database
     */
    private final String url = "jdbc:mysql://localhost:3306/My_project?useUnicode=true&characterEncoding=UTF-8";
    /**
     * password a database
     */
    private final String password ="root";

    /**
     * A connection (session) with a specific database
     */
    private Connection connection;
    /**
     * The object used for executing a static SQL statement
     * and returning the results it produces
     */
    private Statement statement;
    /**
     * A table of data representing a database result set, which is usually generated
     * by executing a statement that queries the database
     */
    private ResultSet resultSet;

    /**
     * This method to get database connection
     */
    public DB_project() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.toString();
        }
    }

    /**
     * This method to add the necessary data to the database
     * @param query Strind
     */
    private void sendQuery(String query) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This constructortor close the database connection
     */
    private void closeDB() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
        } catch (SQLException e) { }
    }

    /**
     * This method to add users to database
     * @param json accepts from the client and parsing
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public void addColomUser(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        String role = (String) jsonObject.get("role");
        String nameuser = (String) jsonObject.get("name");
        String surnameuser = (String) jsonObject.get("surname");
        String patronymicuser = (String) jsonObject.get("patronymic");
        String loginuser = (String) jsonObject.get("login");
        String passworduser = (String) jsonObject.get("password");
        String addUser="insert into users(role,nameuser,surnameuser,patronymicuser,loginuser,passworduser)values("
                + "\"" + role + "\"" +  "," +
                "\"" + nameuser + "\"" +  "," +
                "\"" + surnameuser + "\"" +  "," +
                "\"" + patronymicuser + "\"" +  "," +
                "\"" + loginuser + "\"" +  "," +
                "\"" + passworduser + "\"" + ");" ;
        sendQuery(addUser);
    }

    /**
     * This method checks the existence of users
     * with the specified login and password when registering new users
     * and to transfer role in loginUser
     * @param json accepts from the client and parsing
     * @return  user role
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String selectColomUser(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        String loginuser = (String) jsonObject.get("login");
        String passworduser = (String) jsonObject.get("pass");
        String ans = "null";
        String selectUser="select * from users where loginuser ="
                + "\"" + loginuser + "\"" +
                " and passworduser =" + "\"" + passworduser + "\"" +";";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(selectUser);
            while (resultSet.next()) {
                ans = resultSet.getString("role");
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        return ans;
    }

    /**
     * This method is used to enter user
     * @param json accepts from the client and parsing
     * @return String, this role user
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String loginUser(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        String loginuser = (String) jsonObject.get("login");
        String passworduser = (String) jsonObject.get("pass");
        String role = selectColomUser(json);
        if (role.equals("user")){
            //
            JSONObject resultJson = new JSONObject();
            resultJson.put("idUser", selectIDUser(loginuser,passworduser));
            resultJson.put("role", role);
            resultJson.put("name", selectNameUser(loginuser,passworduser));
            resultJson.put("patronymic", selectPatronymicUser(loginuser,passworduser));
            //
            return resultJson.toString();
        }
        if (role.equals("admin")){
            JSONObject resultJson = new JSONObject();
            resultJson.put("role", role);
            return  resultJson.toString();
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("role", "null");
        return  resultJson.toString();
    }

    /**
     * This method is used to get the id of the desired user
     * and to transfer id in loginUser
     * @param loginuser String
     * @param passworduser string
     * @return user id
     */
    public int selectIDUser(String loginuser, String passworduser){
        int idUser = 0;
        String selectUser="select id from users where loginuser ="
                + "\"" + loginuser + "\"" +
                " and passworduser =" + "\"" + passworduser + "\"" +";";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(selectUser);
            while (resultSet.next()) {

                idUser = Integer.parseInt(resultSet.getString("id"));
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        return idUser;
    }

    /**
     * This method is used to get the name of the desired user
     * and to transfer name in loginUser
     * @param loginuser String
     * @param passworduser String
     * @return user name
     */
    public String selectNameUser(String loginuser, String passworduser){
        String nameUser = null;
        String selectUser="select nameuser from users where loginuser ="
                + "\"" + loginuser + "\"" +
                " and passworduser =" + "\"" + passworduser + "\"" +";";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(selectUser);
            while (resultSet.next()) {
                nameUser = resultSet.getString("nameuser");
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        return nameUser;
    }

    /**
     * This method is used to get the patronymic of the desired user
     * and to transfer patronymic in loginUser
     * @param loginuser String
     * @param passworduser String
     * @return user patronymic
     */
    public String selectPatronymicUser(String loginuser, String passworduser){
        String patronymicUser = null;
        String selectUser="select patronymicuser from users where loginuser ="
                + "\"" + loginuser + "\"" +
                " and passworduser =" + "\"" + passworduser + "\"" +";";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(selectUser);
            while (resultSet.next()) {
                patronymicUser = resultSet.getString("patronymicuser");
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        return patronymicUser;
    }

    /**
     * This method is used to get data about all users of the application
     * in the administrator window
     * @param json accepts from the client and parsing
     * @return parsing json
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String getColomUser(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        String role = (String) jsonObject.get("role");
        String getUser ="select * from users where role="+
                "\"" + role + "\""+ ";";
        JSONArray userJ = new JSONArray();
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(getUser);
            int ind = 0;
            while (resultSet.next()) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("comand", "lookUser");
                resultJson.put("id", resultSet.getInt("id"));
                resultJson.put("role", resultSet.getString("role"));
                resultJson.put("nameuser", resultSet.getString("nameuser"));
                resultJson.put("surnameuser", resultSet.getString("surnameuser"));
                resultJson.put("patronymicuser", resultSet.getString("patronymicuser"));
                userJ.add(ind,resultJson);
                ind++;
            }
        } catch (SQLException e) {

        } finally {
            closeDB();
        }
        return userJ.toString();
    }

    /**
     * This method allows the administrator to delete users
     * @param json accepts from the client and parsing
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public void deleteColomUser(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        Long idUserL = (Long) jsonObject.get("idUser");
        int idUser = idUserL.intValue();
        String del1 = "SET SQL_SAFE_UPDATES = 0";
        String deleteUser ="delete from users where id = "
                + "\"" + idUser + "\""+ ";";
        String del2 = "SET SQL_SAFE_UPDATES = 1";
        sendQuery(del1);
        sendQuery(deleteUser);
        sendQuery(del2);
    }

    /**
     * This method for get the category id
     * @param json accepts from the client and parsing
     * @return parsing json
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String selectIDCosts(String json) throws ParseException {
        JSONObject resultJson = new JSONObject();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        String namecosts = (String) jsonObject.get("namecost");
        String selectCosts="select id from costs where namecosts ="
                + "\"" + namecosts + "\"" +";";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(selectCosts);
            while (resultSet.next()) {
                resultJson.put("id",  Integer.parseInt(resultSet.getString("id")));
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        return resultJson.toString();
    }

    /**
     * This method for add user costs to the database
     * @param json accepts from the client and parsing
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public void addColomBuy(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int iduser = ((Long) jsonObject.get("idUser")).intValue();
        String namebuy = (String) jsonObject.get("nameBuy");
        String pricebuy = (String) jsonObject.get("priceBuy");
        int idcost = ((Long) jsonObject.get("idCost")).intValue();
        LocalDate datebuy = LocalDate.parse((String) jsonObject.get("date"));
        String addBuy="insert into buy(iduser, namebuy, pricebuy, idcost, datebuy)values("
                + "\"" + iduser + "\"" +  "," +
                "\"" + namebuy + "\"" +  "," +
                "\"" + pricebuy + "\"" +  "," +
                "\"" + idcost + "\"" +  "," +
                "\"" + datebuy + "\"" + ");" ;
        sendQuery(addBuy);
    }

    /**
     * This method is used to remove buy
     * @param json accepts from the client and parsing
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public void deleteColomBuy(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int idBuy = ((Long) jsonObject.get("idBuy")).intValue();
        String del1 = "SET SQL_SAFE_UPDATES = 0";
        String deleteBuy ="delete from buy where id = "
                + "\"" +  idBuy + "\""+ ";";
        String del2 = "SET SQL_SAFE_UPDATES = 1";
        sendQuery(del1);
        sendQuery(deleteBuy);
        sendQuery(del2);
    }

    /**
     * This method is used to update buy
     * @param json accepts from the client and parsing
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public void updateColumBuy(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int id = ((Long) jsonObject.get("idBuy")).intValue();
        String namebuy =(String) jsonObject.get("nameBuy");
        String pricebuy =(String) jsonObject.get("priceBuy");
        String del1 = "SET SQL_SAFE_UPDATES = 0";
        String updateBuy = "update buy set namebuy = "
                + "\"" +  namebuy + "\""  + ", pricebuy =" +
                "\"" + pricebuy + "\"" + "where id = "+
                "\"" + id + "\""+ ";" ;
        String del2 = "SET SQL_SAFE_UPDATES = 1";
        sendQuery(del1);
        sendQuery(updateBuy);
        sendQuery(del2);
    }

    /**
     * This method to get the necessary data from the database
     * @param json accepts from the client and parsing
     * @return parsing json
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String getColomBuy(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int idUser = ((Long) jsonObject.get("idUser")).intValue();
        int idCost = ((Long) jsonObject.get("idCost")).intValue();
        LocalDate date = LocalDate.parse((String) jsonObject.get("date"));
        JSONArray userJ = new JSONArray();
        String getBuy ="select * from buy where iduser="
                +"\"" + idUser + "\""  + "and idcost="+
                "\"" +idCost + "\""  + "and datebuy="+
                "\"" + date + "\"" + ";" ;

        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(getBuy);
            int ind = 0;
            while (resultSet.next()) {
                JSONObject resultJson = new JSONObject();

                resultJson.put("comand", "lookBuy");
                resultJson.put("id", resultSet.getInt("id"));
                resultJson.put("idUser", resultSet.getInt("idUser"));
                resultJson.put("namebuy", resultSet.getString("namebuy"));
                resultJson.put("pricebuy", resultSet.getString("pricebuy"));
                resultJson.put("idcost", resultSet.getInt("idCost"));
                resultJson.put("datebuy", resultSet.getDate("datebuy").toString());
                userJ.add(ind,resultJson);
                ind++;
            }
        } catch (SQLException e) {

        } finally {
            closeDB();
        }
        System.out.println(userJ.toString());
        return userJ.toString();
    }

    /**
     * This method for obtaining the necessary data from the database
     * for a certain period of time and calculating the amount
     * of buy for this period
     * @param json accepts from the client and parsing
     * @return parsing json
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String getColomBuyDate(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int idUser = ((Long) jsonObject.get("idUser")).intValue();
        LocalDate dateStart = LocalDate.parse((String) jsonObject.get("dateStart"));
        LocalDate dateEnd = LocalDate.parse((String) jsonObject.get("dateEnd"));
        JSONArray userJ = new JSONArray();
        double summ = 0;
        int ind = 0;
        String getBuyReport ="select * from buy where iduser="
                +"\"" + idUser + "\"" + "and datebuy between"+
                "\"" + dateStart + "\"" +"and"+
                "\"" + dateEnd + "\"" + ";";
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(getBuyReport);

            while (resultSet.next()) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("comand", "lookReportBuy");
                resultJson.put("id", resultSet.getInt("id"));
                resultJson.put("idUser", resultSet.getInt("idUser"));
                resultJson.put("namebuy", resultSet.getString("namebuy"));
                resultJson.put("pricebuy", resultSet.getString("pricebuy"));
                resultJson.put("idcost", resultSet.getInt("idCost"));
                resultJson.put("datebuy", resultSet.getDate("datebuy").toString());
                userJ.add(ind,resultJson);
                ind++;
                summ += Double.parseDouble(resultSet.getString("pricebuy"));
            }
        } catch (SQLException e) {

        } finally {
            closeDB();
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("summ", summ);
        userJ.add(ind,resultJson);
        return userJ.toString();
    }

    /**
     * This method is used to generate a chart for the costs and sum of each category
     * @param json accepts from the client and parsing
     * @return parsing json
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public String getChart(String json) throws ParseException {
        JSONArray userJ = new JSONArray();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int idUser = ((Long) jsonObject.get("id")).intValue();
        LocalDate dateStart = LocalDate.parse((String) jsonObject.get("dateStart"));
        LocalDate dateEnd = LocalDate.parse((String) jsonObject.get("dateEnd"));
        String getCostMax ="select max(id) from costs;";
        int maxCost = 0;
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(getCostMax);
            while (resultSet.next()) {
                maxCost = resultSet.getInt("max(id)");
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        for (int i = 0; i<=maxCost; i++){
            JSONObject resultJson = new JSONObject();
            resultJson.put("idUser", idUser);
            resultJson.put("idCost", i);
            resultJson.put("dateStart", dateStart.toString());
            resultJson.put("dateEnd", dateEnd.toString());
            double summCost = getDiagram(resultJson.toString());
            JSONObject resultJsonChart = new JSONObject();
            resultJsonChart.put("summ", summCost);
            resultJsonChart.put("idCost", i);
            userJ.add(i,resultJsonChart);
        }
        return userJ.toString();
    }

    /**
     * This method to get the buy for each category
     * @param json accepts from the client and parsing
     * @return double sum
     * @throws ParseException signals that an error has been reached unexpectedly while parsing json
     */
    public double getDiagram(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        int idUser = ((Long) jsonObject.get("idUser")).intValue();
        int idCost = ((Long) jsonObject.get("idCost")).intValue();
        LocalDate dateStart = LocalDate.parse((String) jsonObject.get("dateStart"));
        LocalDate dateEnd = LocalDate.parse((String) jsonObject.get("dateEnd"));
        double summCost = 0;
        String getCostDiagram ="select sum(pricebuy) from buy where iduser="
                +"\"" + idUser + "\"" + "and idcost="+
                "\"" + idCost + "\"" +"and datebuy between"+
                "\"" + dateStart + "\"" +"and"+
                "\"" + dateEnd + "\"" + ";";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeBatch();
            resultSet = statement.executeQuery(getCostDiagram);
            while (resultSet.next()) {
                summCost = resultSet.getDouble("sum(pricebuy)");
            }
        } catch (SQLException e) {
        } finally {
            closeDB();
        }
        return summCost;
    }
}
