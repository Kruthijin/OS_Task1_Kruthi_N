import os
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from collections import deque

import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

N = 100
MAX_WORKERS = os.cpu_count() or 4

# Generate 100 x 100 input matrices using TensorFlow.
A = tf.random.uniform((N, N), minval=0, maxval=10, dtype=tf.float32, seed=24)
B = tf.random.uniform((N, N), minval=0, maxval=10, dtype=tf.float32, seed=25)

C = np.zeros((N, N), dtype=np.float32)

class ExecutionRecorder:
    def __init__(self):
        self.lock = threading.Lock()
        self.completed = deque()

    def record(self, i, j):
        with self.lock:
            self.completed.append((i, j))

recorder = ExecutionRecorder()

def calculate_cell(i, j):
    # One submitted task = one result-cell multiplication.
    row = A[i, :]
    col = B[:, j]
    value = tf.tensordot(row, col, axes=1).numpy()
    C[i, j] = value
    recorder.record(i, j)
    return i, j, value

def multiply_with_threads():
    tasks = [(i, j) for i in range(N) for j in range(N)]
    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        futures = [executor.submit(calculate_cell, i, j) for i, j in tasks]
        for future in as_completed(futures):
            future.result()

def animate_result(execution_order):
    fig, axes = plt.subplots(1, 3, figsize=(14, 5))
    axA, axB, axC = axes

    axA.imshow(A.numpy(), cmap="viridis")
    axA.set_title("Matrix A (100 x 100)")
    axB.imshow(B.numpy(), cmap="viridis")
    axB.set_title("Matrix B (100 x 100)")
    result_image = axC.imshow(np.zeros_like(C), cmap="viridis")
    axC.set_title("Matrix C - Thread Progress")

    progress_text = fig.text(0.5, 0.02, "Starting...", ha="center")
    markerA = axA.axhline(0, color="red", linewidth=2)
    markerB = axB.axvline(0, color="red", linewidth=2)

    def update(frame):
        start = frame * 25
        end = min(start + 25, len(execution_order))

        for k in range(start, end):
            i, j = execution_order[k]
            result_image.get_array()[i, j] = C[i, j]

        result_image.set_data(result_image.get_array())

        if end > start:
            i, j = execution_order[end - 1]
            markerA.set_ydata([i, i])
            markerB.set_xdata([j, j])
            progress_text.set_text(
                f"Completed {end:,} / {N*N:,} cells | Current cell: C[{i}][{j}]"
            )
        return result_image, markerA, markerB, progress_text

    frames = (len(execution_order) + 24) // 25
    FuncAnimation(fig, update, frames=frames, interval=80, repeat=False)
    plt.tight_layout(rect=[0, 0.05, 1, 1])
    plt.show()

if __name__ == "__main__":
    print("Matrix multiplication using Python threads + TensorFlow")
    print(f"Matrix size: {N} x {N}")
    print(f"Result-cell tasks: {N*N}")
    print(f"Worker threads: {MAX_WORKERS}")

    multiply_with_threads()

    print("\nTop-left 3 x 3 result:")
    print(C[:3, :3])
    print("\nAll 10,000 result cells completed.")
    print("Starting Matplotlib animation...")

    execution_order = list(recorder.completed)
    animate_result(execution_order)
