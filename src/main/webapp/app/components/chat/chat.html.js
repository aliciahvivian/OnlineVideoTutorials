"use strict";
exports.chatTemplate = "<div class=\"messages\">\n\t<ovt-chatMessage *ngFor=\"let message of messages\" [sender]=\"message.sender\" [message]=\"message.message\" [date]=\"message.date\"></ovt-chatMessage>\n\t\t\t</div>\n<div class=\"mailbox\">\n\t\t\t\t\t<input id=\"myMessage\" [(ngModel)]=\"message\" name=\"message\"type=\"text\" class=\"input-block-level\" placeholder=\"Your message...\" />\n\t\t\t\t\t<input id=\"sendMessage\" (click)=\"sendMessage()\" type=\"submit\" class=\"btn btn-large btn-block btn-primary\"\n\t\t\t\t\t\tvalue=\"Send message\" />\n\t\t\t\t</div>";
// [class]="user.usertType"  
//# sourceMappingURL=chat.html.js.map