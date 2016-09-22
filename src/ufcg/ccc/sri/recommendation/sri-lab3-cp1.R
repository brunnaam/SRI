trim <- function (x) gsub(" ", "", x)

movies <- readline(prompt="Digite o número dos filmes que você gosta separados por vírgula (Ex: 145,156,631,321): ")
movies <- trim(movies)
movies <- as.numeric(unlist(strsplit(movies, ",")))

movies

library(tm)
library(FastKNN)

path = "C:/legendasFinal/"

file.names <- dir(path)
doc.list = c()

for(i in 1:length(file.names)){
  filename <- paste(path, file.names[i], sep="")
  doc <- readChar(filename,file.info(filename)$size)
  doc.list[i] <- doc
}

my.docs <- VectorSource(doc.list)
my.docs$Names <- names(doc.list)

my.corpus <- Corpus(my.docs)
my.corpus <- tm_map(my.corpus, removePunctuation)
my.corpus <- tm_map(my.corpus, stemDocument)
my.corpus <- tm_map(my.corpus, removeNumbers)
my.corpus <- tm_map(my.corpus, stripWhitespace)

term.doc.matrix.stm <- DocumentTermMatrix(my.corpus)
inspect(term.doc.matrix.stm)

term.doc.matrix <- as.matrix(term.doc.matrix.stm)

tfidf.matrix = weightTfIdf(term.doc.matrix.stm)
colnames(tfidf.matrix) <- colnames(term.doc.matrix)

userProfile <- tfidf.matrix[movies, ]
train <- tfidf.matrix[-movies, ]

distance <- Distance_for_KNN_test(userProfile, train)

recommendations <- k.nearest.neighbors(1,distance_matrix = distance, k=10)

for (i in 1:length(recommendations)) { 
  print(recommendations[i])
  print(file.names[recommendations[i]]) 
}

