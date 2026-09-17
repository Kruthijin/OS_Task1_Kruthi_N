# OS Task-1: Multithreading Assignment

**Name:** Kruthi N  
**USN:** NNM24IS112

## Tasks

### 1. Producer-Consumer Problem — Java
File: `ProducerConsumerGUI.java`

- Java multithreading
- Shared circular buffer of capacity 5
- Producer creates values 1–10
- Consumer consumes 10 values
- `synchronized`, `wait()` and `notifyAll()`
- Java Swing GUI to demonstrate the buffer

Run:
```bash
javac ProducerConsumerGUI.java
java ProducerConsumerGUI
```

### 2. Matrix Multiplication — Python + TensorFlow
File: `matrix_multiplication.py`

- 100 × 100 matrices
- 10,000 result-cell tasks
- Each `C[i][j]` is submitted as an individual threaded task
- `ThreadPoolExecutor` manages worker threads
- TensorFlow `tf.tensordot()` calculates each cell
- `threading.Lock()` protects execution-order recording
- Matplotlib animation visualizes the completed cells

Install dependencies:
```bash
pip install tensorflow numpy matplotlib
```

Run:
```bash
python matrix_multiplication.py
```

### Browser Animation
Open `matrix_animation.html` in a browser and press **START**.
It is a small visual illustration of the same cell-wise matrix multiplication concept.

## Suggested GitHub structure
- ProducerConsumerGUI.java
- matrix_multiplication.py
- matrix_animation.html
- README.md
- OS_Task1_Kruthi_N_Report.docx
