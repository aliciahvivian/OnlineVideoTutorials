"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.participantComponentTemplate = "<div class=\"ovt-participant\">\n    <div class=\"video\"> \n        <video [ngClass]=\"setClasses()\" #video id=\"video-{{id}}\" autoplay></video>\n        <ovt-loading  [ngClass]=\"size\" *ngIf=\"loading\"></ovt-loading>\n        <!-- *ngIf=\"!video.src || loading\" -->\n        <div class=\"video-footer ovt-full-name\">{{getFirstName()}}</div>\n    </div>\n</div>";
//# sourceMappingURL=participant.html.js.map