import unidecode

with open('./text.txt', 'r') as f:
    text = f.read().replace('"', '').replace(',', '').replace('.', '').replace('!', '').replace('?', '').split(' ')
    dic = {}
    new_text = ''
    for word in text:
        if dic.__contains__(word):
            if dic[word]:
                continue
        if len(word) > 3:
            new_text += (unidecode.unidecode(word) + ' ')
            dic[word] = 1

    k = open('listaNova.txt', 'w')
    k.write(new_text)
    k.close()
