########################
# Author: Arne Roeters #
########################
args <- commandArgs(trailingOnly = TRUE)
inputFile <- args[1]
groupFile <- args[2]
controlGroup <- args[3]
checkGroup <- args[4]
# Read all data and remove column 2:15
input <- read.table(inputFile, sep=" ", header=T)
groupInput <- read.table(groupFile)
inputFiltered <- input[16:ncol(input)-1]
rownames(inputFiltered) <- input$mpid
rowsToUse <- nrow(input)
rm(input)
# Get all column means
colMean <- apply(inputFiltered[2:ncol(inputFiltered)], 2, mean)
# Get the mean of the total dataset
totalMean <- mean(colMean)
# Normalize the data
inputFiltered[2:ncol(inputFiltered)-1] <- inputFiltered[2:ncol(inputFiltered)-1]/colMean*totalMean
inputFiltered <- rbind(inputFiltered[1:ncol(inputFiltered)], c(groupInput$V1))
inputFiltered <- data.frame(rbind(inputFiltered[1:ncol(inputFiltered)], c(groupInput$V2)))
totalRows <- nrow(inputFiltered)
# Get the means of the groups
groupControl.mean <- apply(inputFiltered[1:rowsToUse, inputFiltered[totalRows,] == controlGroup], 1, mean)
groupToCheck.mean <- apply(inputFiltered[1:rowsToUse, inputFiltered[totalRows,] == checkGroup], 1, mean)
# Calculate logFC
logFC <- log2(groupToCheck.mean/groupControl.mean)
# Get the p-values
pvalues <- -log10(apply(inputFiltered[1:rowsToUse, ], 1, function(x) {
  t.test(x[inputFiltered[totalRows, ] == controlGroup], x[inputFiltered[totalRows, ] == checkGroup])$p.value}))
combined<- cbind(logFC, pvalues)
combined <- data.frame(combined)
filename <- paste("/dataframe", controlGroup, checkGroup, sep="_")
write.csv(combined, paste(args[5], filename, ".csv", sep=""))
