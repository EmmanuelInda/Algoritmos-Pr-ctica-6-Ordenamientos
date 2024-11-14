package algorithm;

import java.util.Comparator;

public class Sort<T> {
	/*
	 * Quick Sort with Hoare's Partition Scheme
	 */
	public void quick(T[] a, int low, int high, Comparator<T> comparator) {
		if (low < high) {
			int pivotIdx = partition(a, low, high, comparator);

			// Recurse on the two partitions
			quick(a, low, pivotIdx, comparator);
			quick(a, pivotIdx + 1, high, comparator);
		}
	}

	private int partition(T[] a, int low, int high, Comparator<T> comparator) {
		T pivot = a[low];
		int i = low - 1;
		int j = high + 1;

		while (true) {
			// Move from left to right until an element >= pivot is found
			do {
				i++;
			} while (comparator.compare(a[i], pivot) < 0);

			// Move from right to left until an element <= pivot is found
			do {
				j--;
			} while (comparator.compare(a[j], pivot) > 0);

			if (i >= j)
				return j;

			swap(a, i, j);
		}
	}



	/*
	 * Merge sort with Comparator
	 */
	public void merge(T[] a, int l, int r, Comparator<T> comparator) {
		if (l < r) {
			int c = (l + r) / 2;
			merge(a, l, c, comparator);
			merge(a, c + 1, r, comparator);
			mergeHelper(a, l, c, r, comparator);
		}
	}

	private void mergeHelper(T[] a, int l, int c, int r, Comparator<T> comparator) {
		int n1 = c - l + 1;
		int n2 = r - c;

		T[] lA = (T[]) new Object[n1];
		T[] rA = (T[]) new Object[n2];

		for (int i = 0; i < n1; ++i) lA[i] = a[l + i];
		for (int i = 0; i < n2; ++i) rA[i] = a[c + 1 + i];

		int i = 0, j = 0, k = l;

		while (i < n1 && j < n2) {
			if (comparator.compare(lA[i], rA[j]) <= 0)
				a[k++] = lA[i++];
			else
				a[k++] = rA[j++];
		}

		while (i < n1) a[k++] = lA[i++];
		while (j < n2) a[k++] = rA[j++];
	}

	/*
	 * Shell sort with Comparator
	 */
	public void shell(T[] a, Comparator<T> comparator) {
		int n = a.length;

		for (int gap = n / 2; gap > 0; gap /= 2) {
			for (int i = gap; i < n; ++i) {
				T temp = a[i];
				int j;

				for (j = i; (j >= gap) && (comparator.compare(a[j - gap], temp) > 0); j -= gap)
					a[j] = a[j - gap];

				a[j] = temp;
			}
		}
	}

	/*
	 * Selection sort with Comparator
	 */
	public void selection(T[] a, Comparator<T> comparator) {
		int n = a.length;

		for (int i = 0; i < (n - 1); ++i) {
			int minIdx = i;

			for (int j = i + 1; j < n; ++j) {
				if (comparator.compare(a[j], a[minIdx]) < 0)
					minIdx = j;
			}

			swap(a, i, minIdx);
		}
	}

	/*
	 * Radix sort for int[]
	 */
	public static void radix(int[] a) {
		int max = getMax(a);
		for (int exp = 1; (max / exp) > 0; exp *= 10) {
			countingSortByDigit(a, exp);
		}
	}

	private static void countingSortByDigit(int[] a, int exp) {
		int n = a.length;
		int[] output = new int[n];
		int[] count = new int[10];

		for (int i = 0; i < n; ++i) {
			int digit = (a[i] / exp) % 10;
			count[digit]++;
		}

		for (int i = 1; i < 10; ++i) {
			count[i] += count[i - 1];
		}

		for (int i = n - 1; i >= 0; --i) {
			int digit = (a[i] / exp) % 10;
			output[count[digit] - 1] = a[i];
			count[digit]--;
		}

		for (int i = 0; i < n; ++i) {
			a[i] = output[i];
		}
	}

	/*
	 * Private helper methods
	 */
	private void swap(T[] a, int i, int j) {
		T temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	private static int getMax(int[] a) {
		int max = a[0];
		for (int i = 1; i < a.length; ++i)
			if (a[i] > max) max = a[i];
		return max;
	}
}
