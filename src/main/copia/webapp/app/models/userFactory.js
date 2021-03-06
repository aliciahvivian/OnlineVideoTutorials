/**
 * @author Juan Antonio Echeverrías Aranda (juanan.echeve@gmail.com)
 *
 */
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var user_1 = require("./user");
/**
 * It allows to instantiate an object of the class User whith a message that
 * the server send to the client when he identifies himself.
 */
var UserFactory = (function () {
    function UserFactory() {
        console.log("% UserFactory");
    }
    UserFactory.createAnUser = function (msg) {
        console.log("UserFactory.createAnUser");
        console.log(msg);
        var propertiesNumber = Object.keys(msg).length;
        var user;
        if (propertiesNumber >= 5) {
            user = new user_1.User(msg.userName, msg.userType, msg.name, msg.surname, msg.email);
            user.userImage = msg.userImage;
        }
        else if (propertiesNumber >= 3) {
            user = new user_1.User(msg.userName, msg.userType, msg.name);
        }
        return user;
    };
    return UserFactory;
}());
exports.UserFactory = UserFactory;
//# sourceMappingURL=userFactory.js.map