function StringBuilder() {
    this.buffer = [];
    this.iCount = -1;
}

StringBuilder.prototype.append = function append(string) {
    this.buffer[++this.iCount] = string;
    return this;
};

StringBuilder.prototype.toString = function toString() {
    return this.buffer.join("");
};

StringBuilder.prototype.getLength = function getLength() {
    return this.buffer.length;
};