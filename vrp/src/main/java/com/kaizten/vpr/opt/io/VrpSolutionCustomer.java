package com.kaizten.vpr.opt.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.kaizten.opt.io.KaiztenSolutionFileConsumer;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.utils.exception.KaiztenException;
import com.kaizten.vrp.opt.core.Vrp;

public class VrpSolutionCustomer implements KaiztenSolutionFileConsumer<RoutesSolution<Vrp>>{

	@Override
	public void accept(RoutesSolution<Vrp> solution, File outputFile) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(solution.toString());
        } catch (IOException exception) {
            throw new KaiztenException(exception);
        }
		
	}

}
