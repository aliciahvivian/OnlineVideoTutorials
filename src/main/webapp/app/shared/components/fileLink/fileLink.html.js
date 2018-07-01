"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.fileLinkTemplate = "<div class=\"ovt-file-link\">\n    <button class=\"ovt-download-btn\" title=\"Descargar\">\n        <a ovt-download class=\"ovt-download-link\" [href]=\"file.downloadUrl\"></a>\n    </button>\n    <span  [ngClass]=\"{'ovt-file-name': validType}\" (click)=\"onLoadFile($event)\" [ovt-commentary-anchor]=\"validType?'Cargar':'No se puede cargar el tipo de archivo'\">{{file.name}}</span>\n</div>\n";
//# sourceMappingURL=fileLink.html.js.map