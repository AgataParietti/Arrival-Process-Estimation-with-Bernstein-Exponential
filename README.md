# Arrival Process Estimation with Bernstein Exponential

This project focuses on estimating a time-varying arrival process using a Bernstein Exponential (BE) approximation. The objective is to characterize, at runtime, the inter-arrival time distribution of requests arriving at an edge component, such as a MEC (Multi-access Edge Computing) node.

## Motivation

In dynamic systems where request rates vary over time, it is useful to estimate the underlying inter-arrival distribution using online data. This estimation can be used for:

- Predicting future request load.
- Supporting adaptive resource allocation.
- Designing proactive service policies.

## Approach

The estimation process is based on the following steps:

1. **Sampling**: Generate or collect arrival timestamps.
2. **Sliding Window**: Use a moving time window to select the most recent inter-arrival samples.
3. **Histogram**: Build a histogram from the selected samples to approximate the probability density.
4. **Bernstein Estimator**: Apply the Bernstein Exponential method using the histogram to compute the approximation coefficients.
5. **Runtime Evaluation**: Use the BE estimator to evaluate the estimated PDF or CDF on-demand.

The degree of the Bernstein approximation is computed as:
`n = (w / log(w))^2`
where `w` is the window size (i.e., number of inter-arrival samples).

## 🛠️ Structure

- `Sampler.java`: Generates arrival timestamps with time-varying intensity.
- `Histogram.java`: Builds the histogram and computes the empirical CDF.
- `BernsteinEstimator.java`: Calculates BE coefficients from histogram data.
- `BernsteinExponential.java`: Represents the BE approximation and allows evaluation.
- `Main.java`: Example usage and CSV export for analysis and plotting.
- `estimate_output.csv`: Output coefficients or evaluation of the BE estimator.
- `histogram_output.csv`: Bin frequencies and empirical CDF.

## Visualization

Python scripts (not included here) can be used to visualize and compare:
- The histogram CDF vs. the BE-estimated CDF.
- Density estimation performance over time.

## 🚀 Future Work

- Integrate the estimator with [Sirio](https://github.com/oris-tool/sirio).
- Refine the adaptive logic for window sizing.
- Evaluate accuracy under different arrival patterns.
