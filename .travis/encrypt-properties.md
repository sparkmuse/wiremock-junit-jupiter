# Follow the following to encrypt all properties needed.

### GPG_EXEC
```shell script
travis encrypt GPG_EXEC="command-here"
```

### NEXUS_PASSWORD
```shell script
travis encrypt NEXUS_PASSWORD='password-here'
```

### NEXUS_USERNAME
```shell script
travis encrypt NEXUS_USERNAME='user-name-here'
```

### GPG_SECRET_KEYS (set online value too large)
```shell script
# to find your keys
gpg --list-keys  
# to export it
gpg -a --export-secret-keys <<secret-key-here>> | base64
# to encrypt it
# this key has to be added online because it's too big
```

### GPG_OWNERTRUST
```shell script
# to find your keys
gpg --list-keys  
# to export it
gpg --export-ownertrust | base64
# to encrypt it
travis encrypt GPG_OWNERTRUST="<<trust-here>>"
```

### GPG_PASSPHRASE
```shell script
travis encrypt GPG_PASSPHRASE='pass-phrase'
```