#include <fstream>
#include <algorithm>
using namespace std;
int a[1000005];
int main() {
    ifstream fin("students.txt");
    ofstream fout("waittime.txt");
    int m, sum, pre;
    double mean;
    fin>>m;
    for(int i = 0; i < m; i++) fin>>a[i];
    sort(a, a + m);
    sum = 0;
    pre = 0;
    for(int i = 0; i < m - 1; i++){
        pre += a[i];
        sum += pre;
    }
    mean = (double)sum / m;
    fout<<mean;
    fin.close();
    fout.close();
    return 0;
}
