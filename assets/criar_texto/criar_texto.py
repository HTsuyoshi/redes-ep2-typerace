with open("./text.txt", "r") as f:
    text = f.read().split(' ')
    dic = {}
    new_text = ""
    for word in text:
        if dic.__contains__(word):
            if dic[word]:
                continue
        if len(word) > 3:
            new_text += (word + " ")
            dic[word] = 1

    k = open("listaNova.txt", "w")
    k.write(new_text)
    k.close()
