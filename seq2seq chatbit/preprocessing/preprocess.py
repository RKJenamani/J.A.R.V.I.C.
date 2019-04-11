from utils import loadPrepareData, batch2TrainData
import random
import pickle

# Load/Assemble voc and pairs


save_dir = "data/"
voc, pairs = loadPrepareData("imput.txt", "my_corpus", "data/input.txt", save_dir)
# Print some pairs to validate
print(len(pairs))
print("\npairs:")
for pair in pairs[:10]:
    print(pair)

# Example for validation
small_batch_size = 5
batches = batch2TrainData(voc, [random.choice(pairs) for _ in range(small_batch_size)])
pickle_out_batches = open("data/tensors.pickle", "wb")
pickle.dump(batches, pickle_out_batches)
input_variable, lengths, target_variable, mask, max_target_len = batches

print("input_variable:", input_variable)
print("lengths:", lengths)
print("target_variable:", target_variable)
print("mask:", mask)
print("max_target_len:", max_target_len)