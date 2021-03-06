package org.codehaus.mojo.cobertura;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.cobertura.tasks.CheckTask;

/**
 * Check the Last Instrumentation Results.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * 
 * @goal check
 * @phase test
 */
public class CoberturaCheckMojo
    extends AbstractCoberturaMojo
{

    public void execute()
        throws MojoExecutionException
    {
        if ( check == null )
        {
            throw new MojoExecutionException( "The Check configuration is missing." );
        }

        if ( !dataFile.exists() )
        {
            throw new MojoExecutionException( "Cannot perform check, instrumentation not performed." );
        }

        CheckTask task = new CheckTask();
        setTaskDefaults( task );
        task.setConfig( check );
        task.setDataFile( dataFile.getAbsolutePath() );

        task.execute();
    }
}
