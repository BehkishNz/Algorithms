PM = [0.02 0.1 0.2 0.33];
PC = [0.6 0.75 0.9];

section_2result = [];
i = 1;
while i <= 3
    j = 1;
    while j <= 4
        m = PM(1, j);
        c = PC(1, i);
        section_2result = [section_2result; 25 50 c m a3q1ResultGen(50, 150, c, m)];
        j = j + 1;
    end
    i = i + 1;
end