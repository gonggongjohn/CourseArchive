#include<iostream>
#include<fstream>

using namespace std;
int a[1000000], t[1000000], n;

void merge(int left, int right) {
    if (right - left <= 1) return;
    int mid = left + (right - left >> 1);
    merge(left, mid);
    merge(mid, right);
    int p = left, q = mid, cur = left;
    while (cur < right) {
        if (p >= mid || (q < right && a[p] > a[q]))
            t[cur++] = a[q++];
        else
            t[cur++] = a[p++];
    }
    for (int i = left; i < right; i++) a[i] = t[i];
}

int main() {
    ifstream fin("data.txt");
    ofstream fout("sorted.txt");
    n = 0;
    while (!fin.eof()) {
        fin >> a[n];
        n++;
    }
    n--;
    merge(0, n);
    for (int w = 0; w < n; w++) {
        fout << a[w] << " ";
    }
    cout<<endl;
    fin.close();
    fout.close();
    return 0;
}