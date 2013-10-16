package de.ddb.next.exception

class FavoritelistNotFoundException extends Exception {

    FavoritelistNotFoundException() {
        super()
    }

    FavoritelistNotFoundException(String description) {
        super(description)
    }
}
