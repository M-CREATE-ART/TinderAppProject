package app.servlets;

import app.dao.UserDao;
import app.entities.User;
import app.tools.TemplateEngine;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class LikedServlet extends HttpServlet {
    UserDao userDao = new UserDao();
    private final TemplateEngine engine;

    public LikedServlet(TemplateEngine engine) throws SQLException {
        this.engine = engine;
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, Object> data = new HashMap<>();
        User me = userDao.getMeFromCookie(req);
        List<User> likedUsers = userDao.getLikedUsers(me);
        data.put("users", likedUsers);
        data.put("act", "like");
        userDao.updateLastLogin(me);
        engine.render("people-list.ftl", data, resp);
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("message");

        if (email!=null) {
            Cookie cookie = new Cookie("message", email);
            cookie.setMaxAge(60 * 60);
            resp.addCookie(cookie);
            userDao.updateLastLogin(userDao.getMeFromCookie(req));
            resp.sendRedirect("/messages");
        }
    }

}
