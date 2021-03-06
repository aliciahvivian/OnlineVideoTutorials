"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var file_service_1 = require("../../../../services/file.service");
var pdfLoader_html_1 = require("./pdfLoader.html");
var PdfLoaderComponent = (function () {
    function PdfLoaderComponent(file) {
        this.file = file;
        console.log("*** new PdfLoaderComponent");
        this.sizeLimit = this.file.sizeLimit;
        this.sharedFile = new core_1.EventEmitter();
        this.newFile = new core_1.EventEmitter();
        this.files = []; //*
    }
    PdfLoaderComponent.prototype.ngOnInit = function () {
        console.log("PdfLoaderComponent.ngOnInit");
        console.log("address: " + this.address);
        this.file.init(this.address);
        console.log("uploadFileUrl: " + this.file.uploadFileUrl);
        this.options = {
            url: this.file.uploadFileUrl
        };
        this.getExistingFiles();
        this.getSharedFiles();
    };
    PdfLoaderComponent.prototype.getExistingFiles = function () {
        var _this = this;
        this.file.getExistingFiles()
            .subscribe(function (files) { _this.files = files.slice(0); });
    };
    PdfLoaderComponent.prototype.getSharedFiles = function () {
        var _this = this;
        this.file.getSharedFiles()
            .subscribe(function (file) {
            _this.files.push(file);
            var fileName = file.name;
            console.log("shared file: " + fileName);
            console.log("uploaded file: " + _this.uploadFile);
            setTimeout(function () {
                if (_this.uploadFile !== fileName) {
                    _this.alertOfANewFile(fileName);
                }
            }, 1500); // Wait for the execution of the handleUpload function
        }, function (error) { return console.log(error); }, function () { return console.log('complete'); });
    };
    PdfLoaderComponent.prototype.beforeUpload = function (uploadingFile) {
        if (uploadingFile.size > this.sizeLimit) {
            uploadingFile.setAbort();
            alert('El archivo no puede pesar más de 2 MB');
        }
    };
    PdfLoaderComponent.prototype.handleUpload = function (data) {
        console.log("handleUpload");
        console.log("handleUpload - data:", data);
        console.log("handleUpload - data.status:", data.status);
        console.log("handleUpload - data.status === 200?:", data.status === 200);
        if (data && data.status === 200) {
            this.uploadFile = data.originalName;
            console.log("uploaded file: " + this.uploadFile);
        }
    };
    PdfLoaderComponent.prototype.onSelectedFile = function (file) {
        console.log("selected file");
        if (this.getExtension(file) === 'pdf') {
            this.sharedFile.emit(file);
        }
        // this.file.downloadFile(file);
    };
    PdfLoaderComponent.prototype.onDownload = function () {
        // The download event trigger the beforeunload event
        console.log('downloadEvent');
        sessionStorage.setItem('downloadEvent', 'true');
    };
    PdfLoaderComponent.prototype.alertOfANewFile = function (fileName) {
        console.log("PdfLoader.alertOfANewFile(" + fileName + ")");
        this.newFile.emit(fileName);
    };
    PdfLoaderComponent.prototype.getExtension = function (fileName) {
        return fileName.split('.').pop();
    };
    PdfLoaderComponent.prototype.ngOnDestroy = function () {
        this.file.destroy();
    };
    return PdfLoaderComponent;
}());
__decorate([
    core_1.Input("ovt-address"),
    __metadata("design:type", String)
], PdfLoaderComponent.prototype, "address", void 0);
__decorate([
    core_1.Output("ovt-file"),
    __metadata("design:type", core_1.EventEmitter)
], PdfLoaderComponent.prototype, "sharedFile", void 0);
__decorate([
    core_1.Output("ovt-new-file"),
    __metadata("design:type", core_1.EventEmitter)
], PdfLoaderComponent.prototype, "newFile", void 0);
PdfLoaderComponent = __decorate([
    core_1.Component({
        moduleId: module.id,
        selector: 'ovt-pdf-loader',
        template: pdfLoader_html_1.pdfLoaderTemplate,
        styleUrls: ['pdfLoader.css'],
    }),
    __metadata("design:paramtypes", [file_service_1.FileService])
], PdfLoaderComponent);
exports.PdfLoaderComponent = PdfLoaderComponent;
//# sourceMappingURL=pdfLoader.component.js.map