# redes-ep2-typerace
Repositório para o EP2 de Redes de Computadores, EACH-USP - 2021/2

# Integrantes
* Guilherme Kendji Ishikawa - 11914650
* Gustavo Pimentel Soares   - 11795961
* Gustavo Tsuyoshi Ariga    - 11857215
* Henrique Tsuyoshi Yara    - 11796083

## Pré-requisitos
* JDK 11 ou maior (testado com a JDK11 OpenJDK)
* Gradle (incluso no repositório, não é necessário instalá-lo)

### Rodando
Para rodar o servidor na porta padrão:
```sh
./gradlew server:run --console=plain
```

Para rodar o servidor em alguma porta específica:
```sh
./gradlew server:run --console=plain --args='123'
```

Para ter uma melhor experiência ao jogar o jogo recomendamos rodar :D :
```sh
./gradlew client:run --console=plain
```
