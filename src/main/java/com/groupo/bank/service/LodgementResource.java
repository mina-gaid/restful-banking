package com.groupo.bank.service;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/lodgement")
@Produces("application/json")
public class LodgementResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    @POST
    @Path("/")
    @Produces("application/json")
    public Response createLodgement(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();
        Connection db = getConnection();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String account = info.getQueryParameters().getFirst("account");
        double amount = Double.parseDouble(info.getQueryParameters().getFirst("amount"));

        Validator v = new Validator();

        if (v.isValidAPI(apiKey) && v.isValidAccountNumber(account)) {

            PreparedStatement p = db.prepareStatement("SELECT status from account where account_number = ?");
            p.setString(1, account);
            ResultSet rs = p.executeQuery();

            if(rs.next()){
                int status = rs.getInt("status");
                if(status == 1){
                    String updateBalance = "UPDATE account SET current_balance = current_balance + ? WHERE account_number = ?";
                   PreparedStatement st3 = db.prepareStatement(updateBalance);
                   st3.setDouble(1, amount);
                   st3.setString(2, account);
                   st3.executeUpdate();

                   PreparedStatement ps = db.prepareStatement("SELECT current_balance, customer_id FROM account WHERE account_number = ?");
                   ps.setString(1, account);
                   ResultSet s = ps.executeQuery();
                   if (s.next()) {
                       int id = s.getInt("customer_id");
                       double balance = s.getDouble("current_balance");


                       TransactionResource t = new TransactionResource();
                       t.addTransaction("Lodgement", balance, id);
                   }

                   return Response.status(200).entity(gson.toJson(new APIResponse("200", "Lodgement complete."))).build();

               } else {
                    return Response.status(200).entity(gson.toJson(new APIResponse("405c", "This account has been removed and therefore cannot be lodged to."))).build();
                }
            }
             else {
            db.close();
            return Response.status(200).entity(gson.toJson(new APIResponse("500", "Invalid API."))).build();
        }

    }
    return null;
    }
}
