#include <iostream>
#include <fstream>
#include <cstdlib>
#include <ctime>
using namespace std;
int main() {
    ofstream fout1("data1.txt");
    ofstream fout2("data2.txt");
    srand(time(0));
    int n1, n2;
    cin>>n1>>n2;
    for(int i = 0; i < n1; i++) fout1<<rand() % 10<<" ";
    for(int i = 0; i < n2; i++) fout2<<rand() % 10<<" ";
    fout1.close();
    fout2.close();
    return 0;
}
