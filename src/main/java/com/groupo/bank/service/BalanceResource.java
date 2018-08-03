package com.groupo.bank.service;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/balance")
@Produces("application/json")
public class BalanceResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    @GET
    @Path("/")
    @Produces("application/json")
    public Response getBalance(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();
        Connection db = getConnection();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String accountNumber = info.getQueryParameters().getFirst("account_number");

        Validator v = new Validator();

        if (v.isValidAPI(apiKey)) {
            PreparedStatement p = db.prepareStatement("SELECT status from account where account_number = ?");
            p.setString(1, accountNumber);
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                int status = rs.getInt("status");
                if (status == 1) {
                    String verifyAccount = "SELECT account_number, current_balance FROM account WHERE account_number = ?";
                    PreparedStatement st2 = db.prepareStatement(verifyAccount);
                    st2.setString(1, accountNumber);
                    ResultSet rs2 = st2.executeQuery();

                    if (rs2.next()) {

                        String account = rs2.getString("account_number");
                        String balance = rs2.getString("current_balance");

                        return Response.status(200).entity(gson.toJson(new Balance(account, balance))).build();

                    }
                } else {
                    return Response.status(200).entity(gson.toJson(new APIResponse("Error", "Cant view the balance of a removed account."))).build();
                }

            } else {
                db.close();
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API key."))).build();
            }

            db.close();
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid account number."))).build();

        }
    return null;

    }
}
